package com.fiap.mechanical_hub.infrastructure.http.mappers;

import com.fiap.mechanical_hub.application.command.stock.CreateStockForNewMaterialCommand;
import com.fiap.mechanical_hub.application.command.stock.DeleteStockCommand;
import com.fiap.mechanical_hub.application.command.stock.FindStockByMaterialIdCommand;
import com.fiap.mechanical_hub.application.command.stock.RegisterStockEntryCommand;
import com.fiap.mechanical_hub.application.command.stock.ReserveStockForServiceOrderCommand;
import com.fiap.mechanical_hub.application.dto.stock.StockDetailResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockEntryRequest;
import com.fiap.mechanical_hub.application.dto.stock.StockMovementResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockSummaryResponse;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class StockHttpMapper {

    private final MaterialRepository materialRepository;

    public StockHttpMapper(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public CreateStockForNewMaterialCommand toCreateStockCommand(UUID materialId) {
        return new CreateStockForNewMaterialCommand(materialId);
    }

    public RegisterStockEntryCommand toRegisterStockEntryCommand(StockEntryRequest request) {
        return new RegisterStockEntryCommand(
            request.materialId(),
            request.quantity()
        );
    }

    public ReserveStockForServiceOrderCommand toReserveCommand(UUID serviceOrderId, UUID materialId, Integer quantity) {
        return new ReserveStockForServiceOrderCommand(serviceOrderId, materialId, quantity);
    }

    public DeleteStockCommand toDeleteCommand(UUID materialId) {
        return new DeleteStockCommand(materialId);
    }

    public FindStockByMaterialIdCommand toFindCommand(UUID materialId) {
        return new FindStockByMaterialIdCommand(materialId);
    }

    public StockSummaryResponse toSummaryResponse(UUID materialId, List<Stock> stocks) {
        int quantityAvailable = stocks.stream()
            .filter(s -> s.getStatus() == StockStatusEnum.AVAILABLE)
            .mapToInt(Stock::getQuantity)
            .sum();

        int quantityReserved = stocks.stream()
            .filter(s -> s.getStatus() == StockStatusEnum.RESERVED)
            .mapToInt(Stock::getQuantity)
            .sum();

        int quantityTotal = quantityAvailable + quantityReserved;

        String materialName = materialRepository.findById(materialId)
            .orElseThrow(() -> new NoSuchElementException("Material não encontrado"))
            .getName();

        return new StockSummaryResponse(
            materialId,
            materialName,
            quantityTotal,
            quantityAvailable,
            quantityReserved
        );
    }

    public List<StockSummaryResponse> buildStockSummary(List<Stock> stocks) {
        Map<UUID, List<Stock>> groupedByMaterial = stocks.stream()
            .collect(Collectors.groupingBy(Stock::getMaterialId));

        return groupedByMaterial.entrySet().stream()
            .map(entry -> toSummaryResponse(entry.getKey(), entry.getValue()))
            .toList();
    }

    public StockDetailResponse toDetailResponse(UUID materialId, List<Stock> stocks, List<StockMovement> movements) {
        int quantityAvailable = stocks.stream()
            .filter(s -> s.getStatus() == StockStatusEnum.AVAILABLE)
            .mapToInt(Stock::getQuantity)
            .sum();

        int quantityReserved = stocks.stream()
            .filter(s -> s.getStatus() == StockStatusEnum.RESERVED)
            .mapToInt(Stock::getQuantity)
            .sum();

        int quantityTotal = quantityAvailable + quantityReserved;

        List<StockMovementResponse> movementResponses = movements.stream()
            .map(movement -> new StockMovementResponse(
                movement.getId(),
                movement.getMaterialId(),
                movement.getServiceOrderId(),
                movement.getMovementType(),
                movement.getQuantity(),
                movement.getCreatedAt()
            ))
            .toList();

        return new StockDetailResponse(
            materialId,
            quantityTotal,
            quantityAvailable,
            quantityReserved,
            movementResponses
        );
    }
}


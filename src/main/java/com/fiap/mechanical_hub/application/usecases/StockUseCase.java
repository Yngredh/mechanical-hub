package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.stock.StockDetailResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockEntryItem;
import com.fiap.mechanical_hub.application.dto.stock.StockMovementResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockSummaryResponse;
import com.fiap.mechanical_hub.application.mappers.StockMovementMapper;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockUseCase {
    private final StockMovementUseCase stockMovementUseCase;
    private final MaterialRepository materialRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public void setStockForNewMaterial(UUID materialId) {
        log.info("Setting stock for new material with ID: {}", materialId);
        Stock stock = Stock.setStockForNewMaterial(materialId);
        stockRepository.save(stock);
    }

    @Transactional
    public void registerStockEntry(List<StockEntryItem> items) {
        log.info("Registering stock entry");

        for (StockEntryItem item : items) {
            materialRepository.findById(item.materialId())
                    .orElseThrow(() -> {
                        log.error("Material not found with ID: {}", item.materialId());
                        return new NotFoundException(
                                "Material não encontrado para o id: " + item.materialId()
                        );
                    });

            Stock stock = stockRepository.findByMaterialIdAndStatus(item.materialId(), StockStatusEnum.AVAILABLE)
                    .orElseThrow(() -> {
                        log.error("Stock not found for material ID: {}", item.materialId());
                        return new NotFoundException(
                                "Estoque não encontrado para o material id: " + item.materialId()
                        );
                    });
            stock.addQuantity(item.quantity());

            stockRepository.save(stock);

            stockMovementUseCase.registerStockEntryMovement(item);

            // TODO: Implementar validação de pendências de estoque
        }

        log.info("Stock entry registration completed");
    }

    @Transactional(readOnly = true)
    public List<StockSummaryResponse> findAll() {
        log.info("Finding all stock summary");
        List<Stock> allStocks = stockRepository.findAll();

        Map<UUID, Map<String, Integer>> aggregatedByMaterial = allStocks.stream()
                .collect(Collectors.groupingBy(
                        Stock::getMaterialId,
                        Collectors.toMap(
                                stock -> stock.getStatus().name(),
                                Stock::getQuantity,
                                Integer::sum
                        )
                ));

        return aggregatedByMaterial.entrySet().stream()
                .map(entry -> {
                    UUID materialId = entry.getKey();
                    Map<String, Integer> statusMap = entry.getValue();

                    int quantityAvailable = statusMap.getOrDefault(StockStatusEnum.AVAILABLE.name(), 0);
                    int quantityReserved = statusMap.getOrDefault(StockStatusEnum.RESERVED.name(), 0);
                    int quantityTotal = quantityAvailable + quantityReserved;

                    String materialName = materialRepository.findById(materialId)
                            .map(Material::getName)
                            .orElse("Unknown");

                    return new StockSummaryResponse(
                            materialId,
                            materialName,
                            quantityTotal,
                            quantityAvailable,
                            quantityReserved
                    );
                }).toList();
    }

    @Transactional(readOnly = true)
    public StockDetailResponse findByMaterialId(UUID materialId) {
        log.info("Finding stock detail for material ID: {}", materialId);

        List<Stock> stocks = stockRepository.findAllByMaterialId(materialId);

        if (stocks.isEmpty()) {
            log.warn("No stock found for material ID: {}", materialId);
            throw new NotFoundException("Estoque não encontrado para o material id: " + materialId);
        }

        int quantityAvailable = stocks.stream()
                .filter(s -> s.getStatus() == StockStatusEnum.AVAILABLE)
                .mapToInt(Stock::getQuantity)
                .sum();

        int quantityReserved = stocks.stream()
                .filter(s -> s.getStatus() == StockStatusEnum.RESERVED)
                .mapToInt(Stock::getQuantity)
                .sum();

        int quantityTotal = quantityAvailable + quantityReserved;

        List<StockMovement> movements = stockMovementRepository.findByMaterialId(materialId);
        List<StockMovementResponse> movementResponses = movements.stream()
                .map(StockMovementMapper::toResponse)
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

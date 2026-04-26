package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.stock.StockDetailResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockEntryItem;
import com.fiap.mechanical_hub.application.dto.stock.StockMovementResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockSummaryResponse;
import com.fiap.mechanical_hub.application.mappers.StockMapper;
import com.fiap.mechanical_hub.application.mappers.StockMovementMapper;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.application.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.application.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockUseCase {
    private final StockMovementUseCase stockMovementUseCase;
    private final StockPendingUseCase stockPendingUseCase;
    private final NotificationUseCase notificationUseCase;
    private final StockMapper stockMapper;

    private final StockMovementRepository stockMovementRepository;
    private final StockRepository stockRepository;

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
        return stockMapper.buildStockSummary(allStocks);
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

    @Transactional
    public boolean reserveMaterials(ServiceOrder order, Material material, Integer quantity) {
        boolean hasStockPending = false;
        UUID materialId = material.getId();
        UUID serviceOrderId = order.getId();

        log.info("Checking stock availability for material {} for service order {}", materialId, serviceOrderId);
        List<Stock> stockRegister = stockRepository.findAllByMaterialId(materialId);

        Optional<Stock> availableStock = stockRegister.stream().findAny().filter(
                register -> register.getStatus() == StockStatusEnum.AVAILABLE);

        if (availableStock.isEmpty() || availableStock.get().checkMaterialAvailability(quantity)) {
            log.warn("No available stock found for material ID: {}", materialId);
            stockPendingUseCase.createStockPendency(order, material);
            return true;
        }

        log.info("Reserving {} units of material {} for service order {}", quantity, materialId, serviceOrderId);

        availableStock.get().subtractQuantity(quantity);
        Stock aftersaveStock = stockRepository.save(availableStock.get());
        validateMinimumStock(material, aftersaveStock);

        Optional<Stock> reservedStock =  stockRegister.stream().findAny().filter(
                register -> register.getStatus() == StockStatusEnum.RESERVED);

        if (reservedStock.isEmpty()) {
            Stock newReservedStock = Stock.createReservedStock(materialId, quantity);
            stockRepository.save(newReservedStock);
            return hasStockPending;
        }

        reservedStock.get().addQuantity(quantity);

        stockRepository.save(reservedStock.get());

        stockMovementUseCase.registerStockReservationMovement(materialId, serviceOrderId, quantity);

        log.info("Successfully reserved stock for service order {}", serviceOrderId);
        return hasStockPending;
    }

    private void validateMinimumStock(Material material, Stock stock) {
        if (stock == null || stock.getStatus() != StockStatusEnum.AVAILABLE) { return; }

        if (stock.getQuantity() <= material.getMinStockQuantity()) {
            log.warn("Material {} has stock below minimum. Available: {}, Minimum: {}",
                    material.getId(), stock.getQuantity(), material.getMinStockQuantity());

            notificationUseCase.sendLowStockAlert(material.getName(), material.getMinStockQuantity());
        }
    }
}

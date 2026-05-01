package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.stock.StockDetailResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockEntryRequest;
import com.fiap.mechanical_hub.application.dto.stock.StockMovementResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockSummaryResponse;
import com.fiap.mechanical_hub.application.mappers.StockMapper;
import com.fiap.mechanical_hub.application.mappers.StockMovementMapper;
import com.fiap.mechanical_hub.application.repositories.MaterialRepository;
import com.fiap.mechanical_hub.application.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.application.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.application.repositories.StockRepository;
import com.fiap.mechanical_hub.domain.entities.*;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
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
    private final ServiceOrderRepository serviceOrderRepository;
    private final MaterialRepository materialRepository;

    @Transactional
    public void setStockForNewMaterial(UUID materialId) {
        log.info("Setting stock for new material with ID: {}", materialId);
        Stock stock = Stock.setStockForNewMaterial(materialId);
        stockRepository.save(stock);
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
    public void registerStockEntry(StockEntryRequest stockEntry) {
        log.info("Registering stock entry for material ID: {}", stockEntry.materialId());

        Stock updatedStock = registerMaterialEntry(stockEntry);
        resolveMaterialPendingIssues(stockEntry.materialId(), updatedStock);

        log.info("Stock entry registration completed for material ID: {}", stockEntry.materialId());
    }

    public void resolveMaterialPendingIssues(UUID materialId, Stock updatedStock) {
        log.info("Checking stock pending issues for material ID: {}", materialId);
        List<StockPendingItem> pendingIssues = stockPendingUseCase.findMaterialStockPendency(materialId);

        for (StockPendingItem pending : pendingIssues) {
            int available = updatedStock.getQuantity();

            if (available <= 0) {
                log.info("No more available stock for material ID: {}, stopping pending resolution", materialId);
                break;
            }

            int pendingQuantity = pending.getQuantity();
            int reservedQuantity = Math.min(available, pendingQuantity);

            log.info("Resolving pending for service order ID: {} | needed: {} | reserving: {}",
                    pending.getServiceOrderId(), pendingQuantity, reservedQuantity);

            executeReservation(pending.getServiceOrderId(), pending.getMaterialId(), reservedQuantity, updatedStock);

            if (reservedQuantity == pendingQuantity) {
                stockPendingUseCase.removePendency(pending);
                 Optional<StockPendingItem> otherPendencyForSameOrder = pendingIssues.stream()
                        .filter(p -> p.getServiceOrderId().equals(
                                pending.getServiceOrderId()) &&
                                !p.getId().equals(pending.getId())
                        )
                        .findAny();
                 if (otherPendencyForSameOrder.isEmpty()) {
                     removeStockPending(pending.getServiceOrderId());
                 }

                log.info("Pending fully resolved for service order ID: {}", pending.getServiceOrderId());
            }
        }
    }

    private void removeStockPending(UUID orderId) {
        ServiceOrder order = serviceOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada para o id: " + orderId));
        order.setHasStockPending(false);
        serviceOrderRepository.save(order);
        log.info("Removed stock pending from service order ID: {}", orderId);
    }

    private Stock registerMaterialEntry(StockEntryRequest stockEntry) {
        Stock stock = stockRepository.findByMaterialIdAndStatus(stockEntry.materialId(), StockStatusEnum.AVAILABLE)
                .orElseThrow(() -> {
                    log.error("Stock not found for material ID: {}", stockEntry.materialId());
                    return new NotFoundException("Estoque não encontrado para o material id: " + stockEntry.materialId());
                });

        stock.addQuantity(stockEntry.quantity());
        Stock updatedStock = stockRepository.save(stock);
        stockMovementUseCase.registerStockEntryMovement(stockEntry);
        return updatedStock;
    }

    private void executeReservation(UUID serviceOrderId, UUID materialId, Integer quantity, Stock availableStock) {
        availableStock.subtractQuantity(quantity);
        stockRepository.save(availableStock);

        Stock reservedStock = stockRepository
                .findByMaterialIdAndStatus(materialId, StockStatusEnum.RESERVED)
                .orElse(null);

        if (reservedStock == null) {
            Stock newReservedStock = Stock.createReservedStock(materialId, quantity);
            stockRepository.save(newReservedStock);
        } else {
            reservedStock.addQuantity(quantity);
            stockRepository.save(reservedStock);
        }

        validateMinimumStock(materialRepository.findById(materialId).orElse(null), availableStock);

        stockMovementRepository.save(StockMovement.registerReservation(materialId, serviceOrderId, quantity));
    }


    @Transactional
    public boolean reserveForServiceOrder(ServiceOrder order, Material material, Integer quantity) {
        UUID materialId = material.getId();
        UUID serviceOrderId = order.getId();

        log.info("Checking stock availability: material {} | order {}", materialId, serviceOrderId);

        Stock availableStock = stockRepository
                .findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE)
                .orElse(null);

        if (availableStock == null || availableStock.checkMaterialAvailability(quantity)) {
            log.warn("Insufficient stock for material {}: creating pendency", materialId);
            stockPendingUseCase.createStockPendency(order, material, quantity);
            return true;
        }

        log.info("Reserving {} units of material {} for order {}", quantity, materialId, serviceOrderId);

        executeReservation(serviceOrderId, materialId, quantity, availableStock);

        log.info("Reservation completed: material {} | order {}", materialId, serviceOrderId);
        return false;
    }

    private void validateMinimumStock(Material material, Stock stock) {
        if (material == null || stock == null || stock.getStatus() != StockStatusEnum.AVAILABLE) { return; }

        if (stock.getQuantity() <= material.getMinStockQuantity()) {
            log.warn("Material {} has stock below minimum. Available: {}, Minimum: {}",
                    material.getId(), stock.getQuantity(), material.getMinStockQuantity());

            notificationUseCase.sendLowStockAlert(material.getName(), material.getMinStockQuantity());
        }
    }

    @Transactional
    public void restoreReservedItems(ServiceOrder order) {
        log.info("Restoring reserved stock items for order {}", order.getId());

        List<OrderTask> tasks = order.getOrderTasks();

        for (OrderTask task : tasks) {
            List<ServiceMaterial> materials = task.getServiceData().getMaterials();

            for (ServiceMaterial sm : materials) {
                Stock reservedStock = stockRepository.findByMaterialIdAndStatus(
                        sm.getMaterial().getId(), StockStatusEnum.RESERVED)
                        .orElseThrow(() -> new NotFoundException("Estoque reservado não encontrado para o material: " + sm.getMaterial().getId()));

                reservedStock.release(sm.getQuantity());
                stockRepository.save(reservedStock);

                Stock availableStock = stockRepository.findByMaterialIdAndStatus(
                        sm.getMaterial().getId(), StockStatusEnum.AVAILABLE)
                        .orElseThrow(() -> new NotFoundException("Estoque disponível não encontrado para o material: " + sm.getMaterial().getId()));

                availableStock.replenish(sm.getQuantity());
                stockRepository.save(availableStock);

                stockMovementUseCase.registerStockReturnMovement(sm.getMaterial().getId(), order.getId(), sm.getQuantity());

                log.info("Restored {} units of material {} from order {}", sm.getQuantity(), sm.getMaterial().getId(), order.getId());
            }
        }
    }
}

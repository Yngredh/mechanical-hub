package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockPendingItemRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
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
public class ResolveMaterialPendingItemsUseCase {

    private final StockPendingItemRepository stockPendingItemRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    @Transactional
    public Stock execute(UUID materialId, Stock updatedStock) {
        log.info("Checking stock pending issues for material ID: {}", materialId);
        List<StockPendingItem> pendingIssues = stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(materialId);

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

            updatedStock = executeReservation(
                pending.getServiceOrderId(),
                pending.getMaterialId(),
                reservedQuantity,
                updatedStock
            );

            if (reservedQuantity == pendingQuantity) {
                stockPendingItemRepository.delete(pending);
                Optional<StockPendingItem> otherPendencyForSameOrder = pendingIssues.stream()
                    .filter(p -> p.getServiceOrderId().equals(pending.getServiceOrderId()) &&
                        !p.getId().equals(pending.getId()))
                    .findAny();
                if (otherPendencyForSameOrder.isEmpty()) {
                    removeStockPending(pending.getServiceOrderId());
                }

                log.info("Pending fully resolved for service order ID: {}", pending.getServiceOrderId());
            }
        }

        return updatedStock;
    }

    private void removeStockPending(UUID orderId) {
        var order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada para o id: " + orderId));
        order.setHasStockPending(false);
        serviceOrderRepository.save(order);
        log.info("Removed stock pending from service order ID: {}", orderId);
    }

    private Stock executeReservation(UUID serviceOrderId, UUID materialId, Integer quantity, Stock availableStock) {
        availableStock.subtractQuantity(quantity);
        Stock savedAvailableStock = stockRepository.save(availableStock);

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

        StockMovement movement = StockMovement.registerReservation(materialId, serviceOrderId, quantity);
        stockMovementRepository.save(movement);

        return savedAvailableStock;
    }
}


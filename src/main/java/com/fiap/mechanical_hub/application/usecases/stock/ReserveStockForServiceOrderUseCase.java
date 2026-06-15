package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.application.command.notification.SendLowStockAlertCommand;
import com.fiap.mechanical_hub.application.command.notification.SendStockShortageAlertCommand;
import com.fiap.mechanical_hub.application.command.stock.ReserveStockForServiceOrderCommand;
import com.fiap.mechanical_hub.application.usecases.notifications.SendLowStockAlertUseCase;
import com.fiap.mechanical_hub.application.usecases.notifications.SendStockShortageAlertUseCase;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockPendingItemRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReserveStockForServiceOrderUseCase {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockPendingItemRepository stockPendingItemRepository;
    private final MaterialRepository materialRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final SendLowStockAlertUseCase sendLowStockAlertUseCase;
    private final SendStockShortageAlertUseCase sendStockShortageAlertUseCase;

    @Transactional
    public Stock execute(ReserveStockForServiceOrderCommand command) {
        UUID materialId = command.materialId();
        UUID serviceOrderId = command.serviceOrderId();

        log.info("Checking stock availability: material {} | order {}", materialId, serviceOrderId);

        Stock availableStock = stockRepository
            .findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE)
            .orElse(null);

        if (availableStock == null || availableStock.checkMaterialAvailability(command.quantity())) {
            log.warn("Insufficient stock for material {}: creating pendency", materialId);
            createStockPendency(serviceOrderId, materialId, command.quantity());
            return null;
        }

        log.info("Reserving {} units of material {} for order {}", command.quantity(), materialId, serviceOrderId);

        Stock reservedStock = executeReservation(serviceOrderId, materialId, command.quantity(), availableStock);

        log.info("Reservation completed: material {} | order {}", materialId, serviceOrderId);
        return reservedStock;
    }

    private void createStockPendency(UUID serviceOrderId, UUID materialId, Integer quantity) {
        StockPendingItem pendingItem = StockPendingItem.create(serviceOrderId, quantity, materialId);
        stockPendingItemRepository.save(pendingItem);

        var material = materialRepository.findById(materialId).orElse(null);
        var order = serviceOrderRepository.findById(serviceOrderId).orElse(null);

        if (material != null && order != null) {
            order.setHasStockPending(true);
            serviceOrderRepository.save(order);
            sendStockShortageAlertUseCase.execute(new SendStockShortageAlertCommand(material.getName(), order.getOrderNumber())
            );
        }
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

        validateMinimumStock(materialId, savedAvailableStock);

        StockMovement movement = StockMovement.registerReservation(materialId, serviceOrderId, quantity);
        stockMovementRepository.save(movement);

        return savedAvailableStock;
    }

    private void validateMinimumStock(UUID materialId, Stock stock) {
        if (stock == null || stock.getStatus() != StockStatusEnum.AVAILABLE) {
            return;
        }

        var material = materialRepository.findById(materialId).orElse(null);
        if (material == null) {
            return;
        }

        if (stock.getQuantity() <= material.getMinStockQuantity()) {
            log.warn("Material {} has stock below minimum. Available: {}, Minimum: {}",
                materialId, stock.getQuantity(), material.getMinStockQuantity());

            sendLowStockAlertUseCase.execute(new SendLowStockAlertCommand(
                    material.getName(), material.getMinStockQuantity())
            );
        }
    }
}


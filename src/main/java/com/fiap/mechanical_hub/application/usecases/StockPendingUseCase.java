package com.fiap.mechanical_hub.application.usecases;


import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.application.repositories.StockPendingItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockPendingUseCase {

    private final NotificationUseCase notificationUseCase;
    private final StockPendingItemRepository stockPendingItemRepository;

    public void createStockPendency(ServiceOrder order, Material material) {
        log.info("Creating stock pendency for service order {} and material {}", order.getId(), material.getId());
        StockPendingItem pendingItem = StockPendingItem.create(order.getId(), material.getId());
        stockPendingItemRepository.save(pendingItem);
        notificationUseCase.sendStockShortageAlert(material.getName(), order.getOrderNumber());
    }
}

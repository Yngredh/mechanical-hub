package com.fiap.mechanical_hub.application.usecases;


import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.domain.repositories.StockPendingItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockPendingUseCase {

    private final NotificationUseCase notificationUseCase;
    private final StockPendingItemRepository stockPendingItemRepository;

    public void createStockPendency(ServiceOrder order, Material material, Integer quantity) {
        log.info("Creating stock pendency for service order {} and material {}", order.getId(), material.getId());
        StockPendingItem pendingItem = StockPendingItem.create(order.getId(), quantity, material.getId());
        stockPendingItemRepository.save(pendingItem);
        notificationUseCase.sendStockShortageAlert(material.getName(), order.getOrderNumber());
    }

    public List<StockPendingItem> findMaterialStockPendency(UUID materialId) {
        return stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(materialId);
    }

    public void removePendency(StockPendingItem pendency) {
        log.info("Removing stock pendency for pending item ID: {}", pendency);
        stockPendingItemRepository.delete(pendency);
        log.info("Stock pendency removed for pending item ID: {}", pendency);
    }

}

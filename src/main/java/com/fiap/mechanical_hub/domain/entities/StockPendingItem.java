package com.fiap.mechanical_hub.domain.entities;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class StockPendingItem {

    private UUID id;
    private UUID serviceOrderId;
    private UUID materialId;
    private LocalDateTime createdAt;

    public StockPendingItem() {
    }

    public StockPendingItem(UUID id, UUID serviceOrderId, UUID materialId, LocalDateTime createdAt) {
        this.id = id;
        this.serviceOrderId = serviceOrderId;
        this.materialId = materialId;
        this.createdAt = createdAt;
    }

    public static StockPendingItem create(UUID serviceOrderId, UUID materialId) {
        return new StockPendingItem(
                UUID.randomUUID(),
                serviceOrderId,
                materialId,
                LocalDateTime.now()
        );
    }
}


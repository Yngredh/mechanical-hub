package com.fiap.mechanical_hub.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class StockPendingItem {

    private UUID id;
    private UUID serviceOrderId;
    private UUID materialId;
    private Integer quantity;
    private LocalDateTime createdAt;

    public StockPendingItem(UUID id, UUID serviceOrderId, UUID materialId, Integer quantity, LocalDateTime createdAt) {
        this.id = id;
        this.serviceOrderId = serviceOrderId;
        this.materialId = materialId;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public static StockPendingItem create(UUID serviceOrderId, Integer quantity, UUID materialId) {
        return new StockPendingItem(
                UUID.randomUUID(),
                serviceOrderId,
                materialId,
                quantity,
                LocalDateTime.now()
        );
    }
}


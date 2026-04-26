package com.fiap.mechanical_hub.infrastructure.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_pending_items")
@Getter
@Setter
public class StockPendingItemModel {

    @Id
    private UUID id;

    @Column(name = "service_order_id", nullable = false)
    private UUID serviceOrderId;

    @Column(name = "material_id", nullable = false)
    private UUID materialId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public StockPendingItemModel() {
    }

    public StockPendingItemModel(UUID id, UUID serviceOrderId, UUID materialId, LocalDateTime createdAt) {
        this.id = id;
        this.serviceOrderId = serviceOrderId;
        this.materialId = materialId;
        this.createdAt = createdAt;
    }
}


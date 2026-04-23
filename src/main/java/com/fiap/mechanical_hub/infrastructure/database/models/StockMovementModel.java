package com.fiap.mechanical_hub.infrastructure.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
public class StockMovementModel {

    @Id
    private UUID id;

    @Column(name = "material_id", nullable = false)
    private UUID materialId;

    @Column(name = "service_order_id", nullable = true)
    private UUID serviceOrderId;

    @Column(name = "movement_type", nullable = false)
    private String movementType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public StockMovementModel() {
    }

    public StockMovementModel(
            UUID id,
            UUID materialId,
            UUID serviceOrderId,
            String movementType,
            Integer quantity,
            LocalDateTime createdAt) {
        this.id = id;
        this.materialId = materialId;
        this.serviceOrderId = serviceOrderId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }
}


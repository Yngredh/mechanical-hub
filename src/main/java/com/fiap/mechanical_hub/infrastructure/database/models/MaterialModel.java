package com.fiap.mechanical_hub.infrastructure.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "materials")
@Getter
@Setter
public class MaterialModel {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "min_stock_quantity", nullable = false)
    private Integer minStockQuantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public MaterialModel() {}

    public MaterialModel(UUID id, String name, String description, BigDecimal unitPrice,
                        Integer minStockQuantity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.minStockQuantity = minStockQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}


package com.fiap.mechanical_hub.infrastructure.database.models;

import com.fiap.mechanical_hub.domain.enums.StockStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock")
@Getter
@Setter
public class StockModel {

    @Id
    private UUID id;

    @Column(name = "material_id", nullable = false, unique = true)
    private UUID materialId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockStatus status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public StockModel() {
    }

    public StockModel(UUID id, UUID materialId, Integer quantity, StockStatus status, LocalDateTime updatedAt) {
        this.id = id;
        this.materialId = materialId;
        this.quantity = quantity;
        this.status = status;
        this.updatedAt = updatedAt;
    }

}


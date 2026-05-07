package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.StockMovementTypeEnum;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class StockMovement {

    private UUID id;
    private UUID materialId;
    private UUID serviceOrderId;
    private String movementType;
    private Integer quantity;
    private LocalDateTime createdAt;

    public StockMovement() {
    }

    public StockMovement(
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

    public static StockMovement buildStockEntryMovement(UUID materialId, Integer quantity) {
        return new StockMovement(
                UUID.randomUUID(),
                materialId,
                null,
                StockMovementTypeEnum.ENTRY.getDescription(),
                quantity,
                LocalDateTime.now()
        );
    }

    public static StockMovement registerReservation(UUID materialId, UUID serviceOrderId, Integer quantity) {
        return new StockMovement(
                UUID.randomUUID(),
                materialId,
                serviceOrderId,
                StockMovementTypeEnum.RESERVED.getDescription(),
                quantity,
                LocalDateTime.now()
        );
    }

    public static StockMovement registerReturn(UUID materialId, UUID serviceOrderId, Integer quantity) {
        return new StockMovement(
                UUID.randomUUID(),
                materialId,
                serviceOrderId,
                StockMovementTypeEnum.RETURN.getDescription(),
                quantity,
                LocalDateTime.now()
        );
    }

    public static StockMovement registerDelete(UUID materialId, UUID serviceOrderId, Integer quantity) {
        return new StockMovement(
                UUID.randomUUID(),
                materialId,
                serviceOrderId,
                StockMovementTypeEnum.EXCLUDED.getDescription(),
                quantity,
                LocalDateTime.now()
        );
    }

    public static StockMovement registerStockOut(UUID materialId, UUID serviceOrderId, Integer quantity) {
        return new StockMovement(
                UUID.randomUUID(),
                materialId,
                serviceOrderId,
                StockMovementTypeEnum.OUT.getDescription(),
                quantity,
                LocalDateTime.now()
        );
    }
}


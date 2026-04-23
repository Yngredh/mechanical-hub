package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.stock.StockMovementResponse;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.infrastructure.database.models.StockMovementModel;

public class StockMovementMapper {

    private StockMovementMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static StockMovementModel toJpaEntity(StockMovement stockMovement) {
        return new StockMovementModel(
                stockMovement.getId(),
                stockMovement.getMaterialId(),
                stockMovement.getServiceOrderId(),
                stockMovement.getMovementType(),
                stockMovement.getQuantity(),
                stockMovement.getCreatedAt()
        );
    }

    public static StockMovement toDomainEntity(StockMovementModel entity) {
        return new StockMovement(
                entity.getId(),
                entity.getMaterialId(),
                entity.getServiceOrderId(),
                entity.getMovementType(),
                entity.getQuantity(),
                entity.getCreatedAt()
        );
    }

    public static StockMovementResponse toResponse(StockMovement stockMovement) {
        return new StockMovementResponse(
                stockMovement.getId(),
                stockMovement.getMaterialId(),
                stockMovement.getServiceOrderId(),
                stockMovement.getMovementType(),
                stockMovement.getQuantity(),
                stockMovement.getCreatedAt()
        );
    }
}

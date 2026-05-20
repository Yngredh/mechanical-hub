package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.infrastructure.database.models.StockMovementModel;

public class StockMovementRepositoryMapper {

    private StockMovementRepositoryMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static StockMovementModel toModel(StockMovement stockMovement) {
        return new StockMovementModel(
                stockMovement.getId(),
                stockMovement.getMaterialId(),
                stockMovement.getServiceOrderId(),
                stockMovement.getMovementType(),
                stockMovement.getQuantity(),
                stockMovement.getCreatedAt()
        );
    }

    public static StockMovement toEntity(StockMovementModel model) {
        return new StockMovement(
                model.getId(),
                model.getMaterialId(),
                model.getServiceOrderId(),
                model.getMovementType(),
                model.getQuantity(),
                model.getCreatedAt()
        );
    }
}


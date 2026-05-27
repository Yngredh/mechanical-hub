package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.infrastructure.database.models.StockPendingItemModel;

public class StockPendingItemRepositoryMapper {

    private StockPendingItemRepositoryMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static StockPendingItemModel toModel(StockPendingItem domain) {
        return new StockPendingItemModel(
                domain.getId(),
                domain.getServiceOrderId(),
                domain.getMaterialId(),
                domain.getQuantity(),
                domain.getCreatedAt()
        );
    }

    public static StockPendingItem toEntity(StockPendingItemModel model) {
        return new StockPendingItem(
                model.getId(),
                model.getServiceOrderId(),
                model.getMaterialId(),
                model.getQuantity(),
                model.getCreatedAt()
        );
    }
}


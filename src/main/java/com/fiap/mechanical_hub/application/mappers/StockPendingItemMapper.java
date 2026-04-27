package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.infrastructure.database.models.StockPendingItemModel;
import org.springframework.stereotype.Component;

@Component
public class StockPendingItemMapper {

    private StockPendingItemMapper() {}

    public static StockPendingItem toDomainEntity(StockPendingItemModel model) {
        return new StockPendingItem(
                model.getId(),
                model.getServiceOrderId(),
                model.getMaterialId(),
                model.getQuantity(),
                model.getCreatedAt()
        );
    }

    public static StockPendingItemModel toJpaEntity(StockPendingItem domain) {
        return new StockPendingItemModel(
                domain.getId(),
                domain.getServiceOrderId(),
                domain.getMaterialId(),
                domain.getQuantity(),
                domain.getCreatedAt()
        );
    }
}


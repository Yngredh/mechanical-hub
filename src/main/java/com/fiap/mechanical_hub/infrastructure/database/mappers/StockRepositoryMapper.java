package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.infrastructure.database.models.StockModel;

public class StockRepositoryMapper {

    private StockRepositoryMapper() {
    }

    public static Stock toDomainEntity(StockModel entity) {
        return new Stock(
                entity.getId(),
                entity.getMaterialId(),
                entity.getQuantity(),
                entity.getStatus(),
                entity.getUpdatedAt()
        );
    }

    public static StockModel toJpaEntity(Stock stock) {
        return new StockModel(
                stock.getId(),
                stock.getMaterialId(),
                stock.getQuantity(),
                stock.getStatus(),
                stock.getUpdatedAt()
        );
    }
}

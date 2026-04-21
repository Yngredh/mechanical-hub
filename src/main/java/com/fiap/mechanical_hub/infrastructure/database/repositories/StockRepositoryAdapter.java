package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatus;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import com.fiap.mechanical_hub.infrastructure.database.models.StockModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StockRepositoryAdapter implements StockRepository {

    private final StockJpaRepository jpaRepository;

    @Override
    public Stock save(Stock stock) {
        StockModel entity = toJpaEntity(stock);
        StockModel saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<Stock> findByMaterialId(UUID materialId) {
        return jpaRepository.findByMaterialId(materialId).map(this::toDomainEntity);
    }

    @Override
    public Optional<Stock> findByMaterialIdAndStatus(UUID materialId, StockStatus status) {
        return jpaRepository.findByMaterialIdAndStatus(materialId, status).map(this::toDomainEntity);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private StockModel toJpaEntity(Stock stock) {
        return new StockModel(
                stock.getId(),
                stock.getMaterialId(),
                stock.getQuantity(),
                stock.getStatus(),
                stock.getUpdatedAt()
        );
    }

    private Stock toDomainEntity(StockModel entity) {
        return new Stock(
                entity.getId(),
                entity.getMaterialId(),
                entity.getQuantity(),
                entity.getStatus(),
                entity.getUpdatedAt()
        );
    }
}


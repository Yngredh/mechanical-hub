package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.application.repositories.StockRepository;
import com.fiap.mechanical_hub.infrastructure.database.models.StockModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.StockJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public Optional<Stock> findByMaterialIdAndStatus(UUID materialId, StockStatusEnum status) {
        return jpaRepository.findByMaterialIdAndStatus(materialId, status).map(this::toDomainEntity);
    }

    @Override
    public List<Stock> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomainEntity)
                .toList();
    }

    @Override
    public List<Stock> findAllByMaterialId(UUID materialId) {
        return jpaRepository.findAllByMaterialId(materialId).stream()
                .map(this::toDomainEntity)
                .toList();
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


package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import com.fiap.mechanical_hub.infrastructure.database.mappers.StockRepositoryMapper;
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
        return StockRepositoryMapper.toDomainEntity(saved);
    }

    @Override
    public Optional<Stock> findById(UUID id) {
        return jpaRepository.findById(id).map(StockRepositoryMapper::toDomainEntity);
    }

    @Override
    public Optional<Stock> findByMaterialIdAndStatus(UUID materialId, StockStatusEnum status) {
        return jpaRepository.findByMaterialIdAndStatus(materialId, status).map(StockRepositoryMapper::toDomainEntity);
    }

    @Override
    public List<Stock> findAll() {
        return jpaRepository.findAll().stream()
                .map(StockRepositoryMapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<Stock> findAllByMaterialId(UUID materialId) {
        return jpaRepository.findAllByMaterialId(materialId).stream()
                .map(StockRepositoryMapper::toDomainEntity)
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

    @Override
    public void deleteByMaterialId(UUID materialId) {
        jpaRepository.findByMaterialId(materialId).ifPresent(stockEntity ->
            jpaRepository.deleteById(stockEntity.getId()));

    }

}


package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.domain.repositories.StockPendingItemRepository;
import com.fiap.mechanical_hub.infrastructure.database.mappers.StockPendingItemRepositoryMapper;
import com.fiap.mechanical_hub.infrastructure.database.models.StockPendingItemModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.StockPendingItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StockPendingItemRepositoryAdapter implements StockPendingItemRepository {

    private final StockPendingItemJpaRepository jpaRepository;

    @Override
    public StockPendingItem save(StockPendingItem stockPendingItem) {
        StockPendingItemModel model = StockPendingItemRepositoryMapper.toModel(stockPendingItem);
        StockPendingItemModel saved = jpaRepository.save(model);
        return StockPendingItemRepositoryMapper.toEntity(saved);
    }

    @Override
    public Optional<StockPendingItem> findById(UUID id) {
        return jpaRepository.findById(id).map(StockPendingItemRepositoryMapper::toEntity);
    }

    @Override
    public List<StockPendingItem> findByMaterialIdOrderByCreatedAtAsc(UUID materialId) {
        return jpaRepository.findByMaterialIdOrderByCreatedAtAsc(materialId)
                .stream()
                .map(StockPendingItemRepositoryMapper::toEntity)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(StockPendingItem stockPendingItem) {
        StockPendingItemModel model = StockPendingItemRepositoryMapper.toModel(stockPendingItem);
        jpaRepository.delete(model);
    }
}


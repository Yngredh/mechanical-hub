package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.domain.repositories.StockPendingItemRepository;
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
        StockPendingItemModel model = toJpaEntity(stockPendingItem);
        StockPendingItemModel saved = jpaRepository.save(model);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<StockPendingItem> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public List<StockPendingItem> findByMaterialIdOrderByCreatedAtAsc(UUID materialId) {
        return jpaRepository.findByMaterialIdOrderByCreatedAtAsc(materialId)
                .stream()
                .map(this::toDomainEntity)
                .toList();
    }

    @Override
    public List<StockPendingItem> findByServiceOrderId(UUID serviceOrderId) {
        return jpaRepository.findByServiceOrderId(serviceOrderId)
                .stream()
                .map(this::toDomainEntity)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(StockPendingItem stockPendingItem) {
        StockPendingItemModel model = toJpaEntity(stockPendingItem);
        jpaRepository.delete(model);
    }

    private StockPendingItemModel toJpaEntity(StockPendingItem stockPendingItem) {
        return new StockPendingItemModel(
                stockPendingItem.getId(),
                stockPendingItem.getServiceOrderId(),
                stockPendingItem.getMaterialId(),
                stockPendingItem.getQuantity(),
                stockPendingItem.getCreatedAt()
        );
    }

    private StockPendingItem toDomainEntity(StockPendingItemModel model) {
        return new StockPendingItem(
                model.getId(),
                model.getServiceOrderId(),
                model.getMaterialId(),
                model.getQuantity(),
                model.getCreatedAt()
        );
    }
}


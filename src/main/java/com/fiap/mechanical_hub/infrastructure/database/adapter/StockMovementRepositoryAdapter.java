package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.infrastructure.database.mappers.StockMovementRepositoryMapper;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.infrastructure.database.models.StockMovementModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.StockMovementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StockMovementRepositoryAdapter implements StockMovementRepository {

    private final StockMovementJpaRepository jpaRepository;

    @Override
    public StockMovement save(StockMovement stockMovement) {
        StockMovementModel model = StockMovementRepositoryMapper.toModel(stockMovement);
        StockMovementModel saved = jpaRepository.save(model);
        return StockMovementRepositoryMapper.toEntity(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<StockMovement> findByMaterialId(UUID materialId) {
        return jpaRepository.findByMaterialIdOrderByCreatedAtDesc(materialId).stream()
                .map(StockMovementRepositoryMapper::toEntity)
                .toList();
    }

}


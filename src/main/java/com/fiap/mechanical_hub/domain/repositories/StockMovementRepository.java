package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.StockMovement;

import java.util.List;
import java.util.UUID;

public interface StockMovementRepository {

    StockMovement save(StockMovement stockMovement);

    void deleteById(UUID id);

    List<StockMovement> findByMaterialId(UUID materialId);

    void deleteByMaterialId(UUID materialId);

    void flush();
}


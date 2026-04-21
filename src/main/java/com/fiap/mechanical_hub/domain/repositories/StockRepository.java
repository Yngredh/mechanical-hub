package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatus;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository {

    Stock save(Stock stock);

    Optional<Stock> findByMaterialId(UUID materialId);

    Optional<Stock> findByMaterialIdAndStatus(UUID materialId, StockStatus status);

    void deleteById(UUID id);
}


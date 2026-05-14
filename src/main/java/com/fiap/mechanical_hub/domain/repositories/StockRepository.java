package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository {

    Stock save(Stock stock);

    Optional<Stock> findByMaterialId(UUID materialId);

    Optional<Stock> findById(UUID id);

    Optional<Stock> findByMaterialIdAndStatus(UUID materialId, StockStatusEnum status);

    List<Stock> findAll();

    List<Stock> findAllByMaterialId(UUID materialId);

    void deleteById(UUID id);

    void deleteByMaterialId(UUID materialId);

    void flush();
}


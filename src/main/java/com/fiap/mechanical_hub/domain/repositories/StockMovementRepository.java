package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.StockMovement;

import java.util.UUID;

public interface StockMovementRepository {

    StockMovement save(StockMovement stockMovement);

    void deleteById(UUID id);
}


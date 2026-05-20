package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindStockMovementsByMaterialId {

    private final StockMovementRepository stockMovementRepository;

    public List<StockMovement> execute(UUID materialId) {
        return stockMovementRepository.findByMaterialId(materialId);
    }
}

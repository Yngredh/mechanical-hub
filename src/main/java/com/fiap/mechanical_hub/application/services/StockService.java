package com.fiap.mechanical_hub.application.services;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public void setStockForNewMaterial(UUID materialId) {
        log.info("Setting stock for new material with ID: {}", materialId);
        Stock stock = Stock.setStockForNewMaterial(materialId);
        stockRepository.save(stock);
    }
}

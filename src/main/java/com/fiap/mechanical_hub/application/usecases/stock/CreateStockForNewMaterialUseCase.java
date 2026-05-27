package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.application.command.stock.CreateStockForNewMaterialCommand;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateStockForNewMaterialUseCase {

    private final StockRepository stockRepository;

    @Transactional
    public Stock execute(CreateStockForNewMaterialCommand command) {
        log.info("Creating stock for new material with ID: {}", command.materialId());

        Stock stock = Stock.setStockForNewMaterial(command.materialId());
        Stock savedStock = stockRepository.save(stock);

        log.info("Stock created for material ID: {}", command.materialId());
        return savedStock;
    }
}


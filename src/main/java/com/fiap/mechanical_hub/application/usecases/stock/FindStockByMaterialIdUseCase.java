package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.application.command.stock.FindStockByMaterialIdCommand;
import com.fiap.mechanical_hub.application.dto.stock.StockDetailResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockMovementResponse;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindStockByMaterialIdUseCase {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    public List<Stock> execute(UUID materialId) {

        log.info("event=FIND_STOCK_DETAIL materialId={}", materialId);

        List<Stock> stocks = stockRepository.findAllByMaterialId(materialId);

        if (stocks.isEmpty()) {
            log.warn("event=STOCK_NOT_FOUND materialId={}", materialId);
            throw new NotFoundException("Estoque não encontrado para o material id: " + materialId);
        }

        return stocks;
    }
}


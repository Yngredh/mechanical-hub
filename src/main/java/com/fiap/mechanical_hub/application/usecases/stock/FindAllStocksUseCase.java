package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindAllStocksUseCase {

    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public List<Stock> execute() {
        log.info("Finding all stocks");
        return stockRepository.findAll();
    }
}


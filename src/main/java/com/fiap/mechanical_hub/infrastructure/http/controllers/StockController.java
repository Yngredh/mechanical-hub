package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.stock.StockDetailResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockEntryRequest;
import com.fiap.mechanical_hub.application.dto.stock.StockSummaryResponse;
import com.fiap.mechanical_hub.application.usecases.StockUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockUseCase stockUseCase;

    @PostMapping("/entry")
    public ResponseEntity<Void> registerEntry(@Valid @RequestBody StockEntryRequest request) {
        stockUseCase.registerStockEntry(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<StockSummaryResponse>> findAll() {
        List<StockSummaryResponse> stockSummaries = stockUseCase.findAll();
        return ResponseEntity.ok(stockSummaries);
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<StockDetailResponse> findByMaterialId(@PathVariable UUID materialId) {
        StockDetailResponse stockDetail = stockUseCase.findByMaterialId(materialId);
        return ResponseEntity.ok(stockDetail);
    }
}


package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.StockStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Stock {

    private UUID id;
    private UUID materialId;
    private Integer quantity;
    private StockStatus status;
    private LocalDateTime updatedAt;

    public Stock() {
    }

    public Stock(UUID id, UUID materialId, Integer quantity, StockStatus status, LocalDateTime updatedAt) {
        this.id = id;
        this.materialId = materialId;
        this.quantity = quantity;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public static Stock setStockForNewMaterial(UUID materialId) {
        Stock stock = new Stock();
        stock.id = UUID.randomUUID();
        stock.materialId = materialId;
        stock.quantity = 0;
        stock.status = StockStatus.AVAILABLE;
        stock.updatedAt = LocalDateTime.now();

        return stock;
    }

    public void markAsReserved(Integer quantityToReserve) {
        if (quantityToReserve > this.quantity) {
            throw new IllegalArgumentException("Quantidade insuficiente para reserva");
        }
        this.status = StockStatus.AVAILABLE;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsAvailable() {
        this.status = StockStatus.AVAILABLE;
        this.updatedAt = LocalDateTime.now();
    }

    public void addQuantity(Integer quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        this.quantity += quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void subtractQuantity(Integer quantity) {
        if (quantity > this.quantity) {
            throw new IllegalArgumentException("Quantidade insuficiente");
        }
        this.quantity -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

}


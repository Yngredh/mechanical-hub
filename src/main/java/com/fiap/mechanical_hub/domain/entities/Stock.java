package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Stock {

    private UUID id;
    private UUID materialId;
    private Integer quantity;
    private StockStatusEnum status;
    private LocalDateTime updatedAt;

    public Stock() {
    }

    public Stock(UUID id, UUID materialId, Integer quantity, StockStatusEnum status, LocalDateTime updatedAt) {
        this.id = id;
        this.materialId = materialId;
        this.quantity = quantity;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public static Stock createReservedStock(UUID materialId, Integer quantity) {
        Stock stock = new Stock();
        stock.id = UUID.randomUUID();
        stock.materialId = materialId;
        stock.quantity = quantity;
        stock.status = StockStatusEnum.RESERVED;
        stock.updatedAt = LocalDateTime.now();

        return stock;
    }

    public static Stock setStockForNewMaterial(UUID materialId) {
        Stock stock = new Stock();
        stock.id = UUID.randomUUID();
        stock.materialId = materialId;
        stock.quantity = 0;
        stock.status = StockStatusEnum.AVAILABLE;
        stock.updatedAt = LocalDateTime.now();

        return stock;
    }

    public void markAsReserved(Integer quantityToReserve) {
        if (quantityToReserve > this.quantity) {
            throw new IllegalArgumentException("Quantidade insuficiente para reserva");
        }
        this.status = StockStatusEnum.RESERVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsAvailable() {
        this.status = StockStatusEnum.AVAILABLE;
        this.updatedAt = LocalDateTime.now();
    }

    public void addQuantity(Integer quantity) {
        if (quantity < 0) { throw new IllegalArgumentException("Quantidade não pode ser negativa"); }
        this.quantity += quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void subtractQuantity(Integer quantity) {
        if (quantity > this.quantity) { throw new IllegalArgumentException("Quantidade insuficiente"); }
        this.quantity -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean checkMaterialAvailability(Integer quantity) {
        return this.getStatus().equals(StockStatusEnum.AVAILABLE) && this.quantity < quantity;
    }

}


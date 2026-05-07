package com.fiap.mechanical_hub.domain.entities.mocks;

import com.fiap.mechanical_hub.domain.entities.Stock;

import static com.fiap.mechanical_hub.domain.entities.constants.TestConstants.*;

public class StockMock {

    public static Stock defaultStock() {
        return Stock.setStockForNewMaterial(DEFAULT_MATERIAL_ID);
    }

    public static Stock availableStock() {
        Stock stock = Stock.setStockForNewMaterial(DEFAULT_MATERIAL_ID);
        stock.addQuantity(DEFAULT_STOCK_QUANTITY);
        return stock;
    }

    public static Stock reservedStock() {
        return Stock.createReservedStock(DEFAULT_MATERIAL_ID, DEFAULT_STOCK_QUANTITY);
    }

    public static Stock stockWithQuantity(Integer quantity) {
        Stock stock = Stock.setStockForNewMaterial(DEFAULT_MATERIAL_ID);
        if (quantity > 0) {
            stock.addQuantity(quantity);
        }
        return stock;
    }

    public static Stock reservedStockWithQuantity(Integer quantity) {
        return Stock.createReservedStock(DEFAULT_MATERIAL_ID, quantity);
    }

}


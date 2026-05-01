package com.fiap.mechanical_hub.domain.entities.mocks;

import com.fiap.mechanical_hub.domain.entities.StockMovement;

import static com.fiap.mechanical_hub.domain.entities.constants.TestConstants.*;

public class StockMovementMock {

    public static StockMovement defaultEntryMovement() {
        return StockMovement.buildStockEntryMovement(DEFAULT_MATERIAL_ID, 10);
    }

    public static StockMovement entryMovementWithQuantity(Integer quantity) {
        return StockMovement.buildStockEntryMovement(DEFAULT_MATERIAL_ID, quantity);
    }

    public static StockMovement reservedMovement() {
        return StockMovement.registerReservation(DEFAULT_MATERIAL_ID, DEFAULT_SERVICE_ORDER_ID, 5);
    }

    public static StockMovement returnMovement() {
        return StockMovement.registerReturn(DEFAULT_MATERIAL_ID, DEFAULT_SERVICE_ORDER_ID, 3);
    }

}


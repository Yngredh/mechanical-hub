package com.fiap.mechanical_hub.domain.entities.mocks;

import com.fiap.mechanical_hub.domain.entities.StockPendingItem;

import static com.fiap.mechanical_hub.domain.entities.constants.TestConstants.*;

public class StockPendingItemMock {

    public static StockPendingItem defaultPendingItem() {
        return StockPendingItem.create(
                DEFAULT_SERVICE_ORDER_ID,
                5,
                DEFAULT_MATERIAL_ID
        );
    }

    public static StockPendingItem pendingItemWithQuantity(Integer quantity) {
        return StockPendingItem.create(
                DEFAULT_SERVICE_ORDER_ID,
                quantity,
                DEFAULT_MATERIAL_ID
        );
    }

    public static StockPendingItem pendingItemWithCustomValues(
            java.util.UUID serviceOrderId, Integer quantity, java.util.UUID materialId) {
        return StockPendingItem.create(serviceOrderId, quantity, materialId);
    }

}


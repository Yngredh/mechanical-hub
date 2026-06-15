package com.fiap.mechanical_hub.mocks.infrastructure.database.models;

import com.fiap.mechanical_hub.infrastructure.database.models.StockPendingItemModel;

import java.time.LocalDateTime;
import java.util.UUID;

public class StockPendingItemModelMock {

    public static final UUID PENDING_ITEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000032");
    public static final UUID SERVICE_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);

    public static StockPendingItemModel withDefaultValues() {
        return new StockPendingItemModel(PENDING_ITEM_ID, SERVICE_ORDER_ID, MATERIAL_ID, 3, CREATED_AT);
    }
}

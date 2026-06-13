package com.fiap.mechanical_hub.mocks.infrastructure.database.models;

import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.StockModel;

import java.time.LocalDateTime;
import java.util.UUID;

public class StockModelMock {

    public static final UUID STOCK_ID = UUID.fromString("00000000-0000-0000-0000-000000000030");
    public static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2024, 1, 2, 10, 0);

    public static StockModel available(int quantity) {
        return new StockModel(STOCK_ID, MATERIAL_ID, quantity, StockStatusEnum.AVAILABLE, UPDATED_AT);
    }

    public static StockModel reserved(int quantity) {
        return new StockModel(STOCK_ID, MATERIAL_ID, quantity, StockStatusEnum.RESERVED, UPDATED_AT);
    }
}

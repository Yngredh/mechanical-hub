package com.fiap.mechanical_hub.mocks.infrastructure.database.models;

import com.fiap.mechanical_hub.infrastructure.database.models.StockMovementModel;

import java.time.LocalDateTime;
import java.util.UUID;

public class StockMovementModelMock {

    public static final UUID MOVEMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000031");
    public static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);

    public static StockMovementModel entrada(int quantity) {
        return new StockMovementModel(MOVEMENT_ID, MATERIAL_ID, null, "entrada", quantity, CREATED_AT);
    }

    public static StockMovementModel reserva(int quantity) {
        UUID serviceOrderId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        return new StockMovementModel(MOVEMENT_ID, MATERIAL_ID, serviceOrderId, "reserva", quantity, CREATED_AT);
    }
}

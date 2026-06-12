package com.fiap.mechanical_hub.mocks.infrastructure.database.models;

import com.fiap.mechanical_hub.infrastructure.database.models.MaterialModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class MaterialModelMock {

    public static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);
    public static final LocalDateTime UPDATED_AT = LocalDateTime.of(2024, 1, 2, 10, 0);

    public static MaterialModel withDefaultValues() {
        return new MaterialModel(
                MATERIAL_ID,
                "Óleo de motor",
                "Óleo 5W30 sintético",
                BigDecimal.valueOf(45.00),
                10,
                CREATED_AT,
                UPDATED_AT
        );
    }
}

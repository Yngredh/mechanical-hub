package com.fiap.mechanical_hub.mocks.domain.entities;

import com.fiap.mechanical_hub.domain.entities.Material;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class MaterialMock {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    public static Material withSufficientStock() {
        return new Material(
                MATERIAL_ID,
                "Óleo de motor",
                "Óleo 5W30 sintético",
                BigDecimal.valueOf(45.00),
                10,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Material withInsufficientStock() {
        return new Material(
                MATERIAL_ID,
                "Filtro de ar",
                "Filtro de ar original",
                BigDecimal.valueOf(25.00),
                20,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Material withPrice(BigDecimal unitPrice) {
        return new Material(
                MATERIAL_ID,
                "Material genérico",
                "Material para teste",
                unitPrice,
                5,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Material withMinStockQuantity(Integer minStockQuantity) {
        return new Material(
                MATERIAL_ID,
                "Material com estoque mínimo",
                "Teste estoque mínimo",
                BigDecimal.valueOf(30.00),
                minStockQuantity,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}


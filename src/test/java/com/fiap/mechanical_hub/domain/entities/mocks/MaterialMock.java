package com.fiap.mechanical_hub.domain.entities.mocks;

import com.fiap.mechanical_hub.domain.entities.Material;

import static com.fiap.mechanical_hub.domain.entities.constants.TestConstants.*;

public class MaterialMock {

    public static Material defaultMaterial() {
        return Material.create(
                DEFAULT_MATERIAL_NAME,
                DEFAULT_MATERIAL_DESCRIPTION,
                DEFAULT_MATERIAL_UNIT_PRICE,
                DEFAULT_MATERIAL_MIN_STOCK
        );
    }

    public static Material materialWithCustomValues(String name, String description) {
        return Material.create(
                name,
                description,
                DEFAULT_MATERIAL_UNIT_PRICE,
                DEFAULT_MATERIAL_MIN_STOCK
        );
    }

    public static Material materialWithHighPrice() {
        return Material.create(
                "Material Premium",
                "Material de alta qualidade",
                DEFAULT_MATERIAL_UNIT_PRICE.multiply(java.math.BigDecimal.valueOf(2)),
                DEFAULT_MATERIAL_MIN_STOCK
        );
    }

    public static Material materialWithLowMinStock() {
        return Material.create(
                DEFAULT_MATERIAL_NAME,
                DEFAULT_MATERIAL_DESCRIPTION,
                DEFAULT_MATERIAL_UNIT_PRICE,
                1
        );
    }

}


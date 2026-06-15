package com.fiap.mechanical_hub.mocks.domain.entities;

import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import java.util.UUID;

public class ServiceMaterialMock {

    private static final UUID SERVICE_MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000050");
    private static final UUID SERVICE_ID = UUID.fromString("00000000-0000-0000-0000-000000000040");

    public static ServiceMaterial withDefaultValues() {
        return new ServiceMaterial(
                SERVICE_MATERIAL_ID,
                SERVICE_ID,
                MaterialMock.withSufficientStock(),
                2
        );
    }

    public static ServiceMaterial withQuantity(int quantity) {
        return new ServiceMaterial(
                SERVICE_MATERIAL_ID,
                SERVICE_ID,
                MaterialMock.withSufficientStock(),
                quantity
        );
    }

}


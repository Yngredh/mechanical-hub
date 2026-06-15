package com.fiap.mechanical_hub.mocks.infrastructure.database.models;

import com.fiap.mechanical_hub.infrastructure.database.models.CustomerModel;
import com.fiap.mechanical_hub.infrastructure.database.models.VehicleModel;

import java.time.LocalDateTime;
import java.util.UUID;

public class VehicleModelMock {

    public static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000070");
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2024, 1, 2, 10, 0);

    public static VehicleModel withDefaultValues() {
        CustomerModel customer = CustomerModelMock.withDefaultValues();
        return new VehicleModel(
                VEHICLE_ID,
                customer,
                "ABC1234",
                "Toyota",
                "Corolla",
                2022,
                "Prata",
                CREATED_AT,
                UPDATED_AT,
                null
        );
    }
}

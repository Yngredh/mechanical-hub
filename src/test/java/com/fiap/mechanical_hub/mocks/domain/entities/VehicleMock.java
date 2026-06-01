package com.fiap.mechanical_hub.mocks.domain.entities;

import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.valueobjects.LicensePlate;
import java.time.LocalDateTime;
import java.util.UUID;

public class VehicleMock {

    private static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000070");
    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000060");

    public static Vehicle withDefaultValues() {
        return new Vehicle(
                VEHICLE_ID,
                CUSTOMER_ID,
                new LicensePlate("ABC1234"),
                "Toyota",
                "Corolla",
                2022,
                "Prata",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    public static Vehicle withLicensePlate(String licensePlate) {
        return new Vehicle(
                VEHICLE_ID,
                CUSTOMER_ID,
                new LicensePlate(licensePlate),
                "Toyota",
                "Corolla",
                2022,
                "Prata",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    public static Vehicle inactive() {
        return new Vehicle(
                VEHICLE_ID,
                CUSTOMER_ID,
                new LicensePlate("ABC1234"),
                "Toyota",
                "Corolla",
                2022,
                "Prata",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}


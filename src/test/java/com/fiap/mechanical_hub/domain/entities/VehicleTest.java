package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import com.fiap.mechanical_hub.domain.valueobjects.LicensePlate;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleTest {

    @Test
    void shouldCreateVehicle_withValidData() {
        UUID customerId = UUID.randomUUID();
        LicensePlate licensePlate = new LicensePlate("ABC1234");

        Vehicle vehicle = Vehicle.create(
                customerId,
                licensePlate,
                "Toyota",
                "Corolla",
                2022,
                "Prata"
        );

        assertThat(vehicle.getId()).isNotNull();
        assertThat(vehicle.getCustomerId()).isEqualTo(customerId);
        assertThat(vehicle.getBrand()).isEqualTo("Toyota");
        assertThat(vehicle.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateVehicle_withNewData() {
        Vehicle vehicle = VehicleMock.withDefaultValues();

        vehicle.update("Honda", "Civic", 2023, "Azul");

        assertThat(vehicle.getBrand()).isEqualTo("Honda");
        assertThat(vehicle.getModel()).isEqualTo("Civic");
        assertThat(vehicle.getYear()).isEqualTo(2023);
        assertThat(vehicle.getColor()).isEqualTo("Azul");
    }

    @Test
    void shouldDeactivateVehicle_settingDeletedAt() {
        Vehicle vehicle = VehicleMock.withDefaultValues();

        vehicle.deactivate();

        assertThat(vehicle.getDeletedAt()).isNotNull();
        assertThat(vehicle.isActive()).isFalse();
    }

    @Test
    void shouldReturnTrue_whenVehicleIsActive() {
        Vehicle vehicle = VehicleMock.withDefaultValues();

        assertThat(vehicle.isActive()).isTrue();
    }

    @Test
    void shouldReturnFalse_whenVehicleIsInactive() {
        Vehicle vehicle = VehicleMock.inactive();

        assertThat(vehicle.isActive()).isFalse();
    }

    @Test
    void shouldMaintainIdAfterUpdate() {
        Vehicle vehicle = VehicleMock.withDefaultValues();
        var originalId = vehicle.getId();

        vehicle.update("Novo brand", "Novo model", 2024, "Cor");

        assertThat(vehicle.getId()).isEqualTo(originalId);
    }

    @Test
    void shouldMaintainLicensePlateAfterUpdate() {
        Vehicle vehicle = VehicleMock.withDefaultValues();
        LicensePlate originalPlate = vehicle.getLicensePlate();

        vehicle.update("Novo brand", "Novo model", 2024, "Cor");

        assertThat(vehicle.getLicensePlate()).isEqualTo(originalPlate);
    }
}


package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.command.vehicle.CreateVehicleCommand;
import com.fiap.mechanical_hub.application.command.vehicle.UpdateVehicleCommand;
import com.fiap.mechanical_hub.application.dto.vehicle.InsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.UpdateVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleMapperTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000060");
    private static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000070");

    @Test
    void shouldMapAllFields_whenConvertingInsertRequestToCreateVehicleCommand() {
        InsertVehicleRequest request = new InsertVehicleRequest("ABC1234", "Toyota", "Corolla", 2022, "Prata");

        CreateVehicleCommand command = VehicleMapper.toCreateVehicleCommand(request, CUSTOMER_ID);

        assertThat(command.customerId()).isEqualTo(CUSTOMER_ID);
        assertThat(command.licensePlate()).isEqualTo(request.getLicensePlate());
        assertThat(command.brand()).isEqualTo(request.getBrand());
        assertThat(command.model()).isEqualTo(request.getModel());
        assertThat(command.year()).isEqualTo(request.getYear());
        assertThat(command.color()).isEqualTo(request.getColor());
    }

    @Test
    void shouldMapAllFields_whenConvertingUpdateRequestToUpdateVehicleCommand() {
        UpdateVehicleRequest request = new UpdateVehicleRequest("Honda", "Civic", 2023, "Preto");

        UpdateVehicleCommand command = VehicleMapper.toUpdateVehicleCommand(VEHICLE_ID, request);

        assertThat(command.id()).isEqualTo(VEHICLE_ID);
        assertThat(command.brand()).isEqualTo(request.getBrand());
        assertThat(command.model()).isEqualTo(request.getModel());
        assertThat(command.year()).isEqualTo(request.getYear());
        assertThat(command.color()).isEqualTo(request.getColor());
    }

    @Test
    void shouldMapAllFields_whenConvertingVehicleToVehicleResponse() {
        Vehicle vehicle = VehicleMock.withDefaultValues();

        VehicleResponse response = VehicleMapper.toVehicleResponse(vehicle);

        assertThat(response.getId()).isEqualTo(vehicle.getId());
        assertThat(response.getCustomerId()).isEqualTo(vehicle.getCustomerId());
        assertThat(response.getLicensePlate()).isEqualTo(vehicle.getLicensePlate().getValue());
        assertThat(response.getBrand()).isEqualTo(vehicle.getBrand());
        assertThat(response.getModel()).isEqualTo(vehicle.getModel());
        assertThat(response.getYear()).isEqualTo(vehicle.getYear());
        assertThat(response.getColor()).isEqualTo(vehicle.getColor());
    }
}

package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.application.command.vehicle.UpdateVehicleCommand;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateVehicleUseCaseTest {

    private static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000070");

    private final VehicleRepository vehicleRepository = mock(VehicleRepository.class);
    private final UpdateVehicleUseCase useCase = new UpdateVehicleUseCase(vehicleRepository);

    @Test
    void shouldReturnUpdatedVehicleResponse_whenVehicleExists() {
        UpdateVehicleCommand command = new UpdateVehicleCommand(VEHICLE_ID, "Honda", "Civic", 2023, "Preto");
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(VehicleMock.withDefaultValues()));
        when(vehicleRepository.save(any())).thenReturn(VehicleMock.withDefaultValues());

        VehicleResponse result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(VEHICLE_ID);
    }

    @Test
    void shouldThrowNotFoundException_whenVehicleDoesNotExist() {
        UpdateVehicleCommand command = new UpdateVehicleCommand(VEHICLE_ID, "Honda", "Civic", 2023, "Preto");
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(VEHICLE_ID.toString());
    }

    @Test
    void shouldSaveVehicle_afterUpdating() {
        UpdateVehicleCommand command = new UpdateVehicleCommand(VEHICLE_ID, "Honda", "Civic", 2023, "Preto");
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(VehicleMock.withDefaultValues()));
        when(vehicleRepository.save(any())).thenReturn(VehicleMock.withDefaultValues());

        useCase.execute(command);

        verify(vehicleRepository).save(any());
    }
}

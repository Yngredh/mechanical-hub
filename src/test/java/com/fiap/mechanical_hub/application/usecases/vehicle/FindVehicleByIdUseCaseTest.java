package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindVehicleByIdUseCaseTest {

    private static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000070");

    private final VehicleRepository vehicleRepository = mock(VehicleRepository.class);
    private final FindVehicleByIdUseCase useCase = new FindVehicleByIdUseCase(vehicleRepository);

    @Test
    void shouldReturnVehicleResponse_whenVehicleExists() {
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(VehicleMock.withDefaultValues()));

        VehicleResponse result = useCase.execute(VEHICLE_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(VEHICLE_ID);
    }

    @Test
    void shouldThrowNotFoundException_whenVehicleDoesNotExist() {
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(VEHICLE_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(VEHICLE_ID.toString());
    }
}

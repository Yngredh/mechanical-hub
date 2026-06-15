package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindAllVehiclesUseCaseTest {

    private final VehicleRepository vehicleRepository = mock(VehicleRepository.class);
    private final FindAllVehiclesUseCase useCase = new FindAllVehiclesUseCase(vehicleRepository);

    @Test
    void shouldReturnAllVehicles_whenVehiclesExist() {
        when(vehicleRepository.findAll()).thenReturn(List.of(VehicleMock.withDefaultValues(), VehicleMock.withLicensePlate("XYZ9A87")));

        List<VehicleResponse> result = useCase.execute();

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnEmptyList_whenNoVehiclesExist() {
        when(vehicleRepository.findAll()).thenReturn(List.of());

        List<VehicleResponse> result = useCase.execute();

        assertThat(result).isEmpty();
    }
}

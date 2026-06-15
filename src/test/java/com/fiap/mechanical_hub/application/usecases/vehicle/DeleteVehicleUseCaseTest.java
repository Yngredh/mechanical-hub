package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteVehicleUseCaseTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000060");

    private final VehicleRepository vehicleRepository = mock(VehicleRepository.class);
    private final DeleteVehicleUseCase useCase = new DeleteVehicleUseCase(vehicleRepository);

    @Test
    void shouldDeactivateAndSaveAllVehicles_whenVehiclesExistForCustomer() {
        Vehicle vehicle = VehicleMock.withDefaultValues();
        when(vehicleRepository.findAllVehiclesByCustomerId(CUSTOMER_ID)).thenReturn(List.of(vehicle));

        useCase.execute(CUSTOMER_ID);

        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void shouldNotSaveAnyVehicle_whenNoVehiclesFoundForCustomer() {
        when(vehicleRepository.findAllVehiclesByCustomerId(CUSTOMER_ID)).thenReturn(List.of());

        useCase.execute(CUSTOMER_ID);

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void shouldNotThrowException_whenNoVehiclesFoundForCustomer() {
        when(vehicleRepository.findAllVehiclesByCustomerId(CUSTOMER_ID)).thenReturn(List.of());

        assertThatCode(() -> useCase.execute(CUSTOMER_ID)).doesNotThrowAnyException();
    }
}

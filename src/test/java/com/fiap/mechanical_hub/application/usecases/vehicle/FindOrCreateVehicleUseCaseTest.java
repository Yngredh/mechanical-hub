package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.domain.service.VehicleDomainService;
import com.fiap.mechanical_hub.domain.valueobjects.LicensePlate;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindOrCreateVehicleUseCaseTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000060");
    private static final String LICENSE_PLATE = "ABC1234";

    private final VehicleRepository vehicleRepository = mock(VehicleRepository.class);
    private final VehicleDomainService vehicleDomainService = mock(VehicleDomainService.class);
    private final FindOrCreateVehicleUseCase useCase = new FindOrCreateVehicleUseCase(vehicleRepository, vehicleDomainService);

    @Test
    void shouldReturnExistingVehicle_whenLicensePlateAlreadyExists() {
        Vehicle existing = VehicleMock.withDefaultValues();
        when(vehicleRepository.findByLicensePlate(LICENSE_PLATE)).thenReturn(Optional.of(existing));

        Vehicle result = useCase.execute(CUSTOMER_ID, LICENSE_PLATE, "Toyota", "Corolla", 2022, "Prata");

        assertThat(result).isEqualTo(existing);
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void shouldCreateAndReturnNewVehicle_whenLicensePlateDoesNotExist() {
        Vehicle newVehicle = VehicleMock.withDefaultValues();
        when(vehicleRepository.findByLicensePlate(LICENSE_PLATE)).thenReturn(Optional.empty());
        when(vehicleDomainService.createLicensePlate(LICENSE_PLATE)).thenReturn(new LicensePlate(LICENSE_PLATE));
        when(vehicleRepository.save(any())).thenReturn(newVehicle);

        Vehicle result = useCase.execute(CUSTOMER_ID, LICENSE_PLATE, "Toyota", "Corolla", 2022, "Prata");

        assertThat(result).isNotNull();
        verify(vehicleRepository).save(any());
    }
}

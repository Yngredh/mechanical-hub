package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.application.command.vehicle.CreateVehicleCommand;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.exceptions.DuplicateLicensePlateException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.domain.service.VehicleDomainService;
import com.fiap.mechanical_hub.domain.valueobjects.LicensePlate;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateVehicleUseCaseTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000060");

    private final VehicleRepository vehicleRepository = mock(VehicleRepository.class);
    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final VehicleDomainService vehicleDomainService = mock(VehicleDomainService.class);
    private final CreateVehicleUseCase useCase = new CreateVehicleUseCase(vehicleRepository, customerRepository, vehicleDomainService);

    @Test
    void shouldReturnVehicleResponse_whenCommandIsValid() {
        CreateVehicleCommand command = new CreateVehicleCommand(CUSTOMER_ID, "ABC1234", "Toyota", "Corolla", 2022, "Prata");
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(mock(com.fiap.mechanical_hub.domain.entities.Customer.class)));
        when(vehicleDomainService.createLicensePlate("ABC1234")).thenReturn(new LicensePlate("ABC1234"));
        when(vehicleRepository.save(any())).thenReturn(VehicleMock.withDefaultValues());

        VehicleResponse result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getLicensePlate()).isEqualTo("ABC1234");
    }

    @Test
    void shouldThrowNotFoundException_whenCustomerDoesNotExist() {
        CreateVehicleCommand command = new CreateVehicleCommand(CUSTOMER_ID, "ABC1234", "Toyota", "Corolla", 2022, "Prata");
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(CUSTOMER_ID.toString());
    }

    @Test
    void shouldThrowDuplicateLicensePlateException_whenLicensePlateAlreadyExists() {
        CreateVehicleCommand command = new CreateVehicleCommand(CUSTOMER_ID, "ABC1234", "Toyota", "Corolla", 2022, "Prata");
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(mock(com.fiap.mechanical_hub.domain.entities.Customer.class)));
        when(vehicleDomainService.createLicensePlate("ABC1234"))
                .thenThrow(new DuplicateLicensePlateException("Veículo com placa ABC1234 já existe"));

        assertThatThrownBy(() -> useCase.execute(command)).isInstanceOf(DuplicateLicensePlateException.class);
    }
}

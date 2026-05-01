package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.vehicle.UpsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.mappers.VehicleMapper;
import com.fiap.mechanical_hub.application.repositories.CustomerRepository;
import com.fiap.mechanical_hub.application.repositories.VehicleRepository;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.exceptions.DuplicateLicensePlateException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidLicensePlateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleUseCaseTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Spy
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleUseCase vehicleUseCase;

    @Test
    @DisplayName("Deve criar um veículo com sucesso")
    void shouldCreateVehicleSuccessfully() {
        UUID customerId = UUID.randomUUID();
        UpsertVehicleRequest request = new UpsertVehicleRequest("ABC1234", "Fiat", "Uno", 2010, "Escada");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(new Customer()));
        when(vehicleRepository.existsByLicensePlate("ABC1234")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        VehicleResponse response = vehicleUseCase.create(customerId, request);

        assertThat(response).isNotNull();
        assertThat(response.getLicensePlate()).isEqualTo("ABC1234");
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não existe na criação")
    void shouldThrowExceptionWhenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        UpsertVehicleRequest request = new UpsertVehicleRequest("ABC1234", "Fiat", "Uno", 2010, "Prata");

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleUseCase.create(customerId, request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a placa for inválida")
    void shouldThrowExceptionForInvalidPlate() {
        UUID customerId = UUID.randomUUID();
        UpsertVehicleRequest request = new UpsertVehicleRequest("PLACA-INVALIDA", "Ford", "Ka", 2020, "Preto");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(new Customer()));

        assertThatThrownBy(() -> vehicleUseCase.create(customerId, request))
                .isInstanceOf(InvalidLicensePlateException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção para placa duplicada na criação")
    void shouldThrowExceptionForDuplicatePlateOnCreate() {
        UUID customerId = UUID.randomUUID();
        UpsertVehicleRequest request = new UpsertVehicleRequest("ABC1234", "Fiat", "Uno", 2010, "Prata");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(new Customer()));
        when(vehicleRepository.existsByLicensePlate("ABC1234")).thenReturn(true);

        assertThatThrownBy(() -> vehicleUseCase.create(customerId, request))
                .isInstanceOf(DuplicateLicensePlateException.class);
    }

    @Test
    @DisplayName("Deve atualizar um veículo com sucesso")
    void shouldUpdateVehicleSuccessfully() {
        UUID vehicleId = UUID.randomUUID();
        var existingVehicle = Vehicle.builder()
                .id(vehicleId)
                .licensePlate("OLD1234")
                .brand("Honda")
                .model("Civic")
                .year(2018)
                .color("Preto")
                .build();

        UpsertVehicleRequest updateRequest = new UpsertVehicleRequest("NEW1234", "Toyota", "Corolla", 2022, "Branco");

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(vehicleRepository.existsByLicensePlateAndIdNot("NEW1234", vehicleId)).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        VehicleResponse response = vehicleUseCase.update(vehicleId, updateRequest);

        assertThat(response.getLicensePlate()).isEqualTo("NEW1234");
        assertThat(response.getModel()).isEqualTo("Corolla");
    }

    @Test
    @DisplayName("Deve buscar por placa existente ou criar um novo veículo")
    void shouldFindByPlateOrCreateNew() {
        UUID customerId = UUID.randomUUID();
        String plate = "ABC1234";
        when(vehicleRepository.findByLicensePlate("ABC1234")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        Vehicle result = vehicleUseCase.findByLicensePlateOrCreate(customerId, plate, "VW", "Gol", 2015, "Azul");

        assertThat(result).isNotNull();
        assertThat(result.getLicensePlate()).isEqualTo("ABC1234");
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve deletar veículo com sucesso")
    void shouldDeleteVehicle() {
        UUID vehicleId = UUID.randomUUID();
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(new Vehicle()));

        vehicleUseCase.delete(vehicleId);

        verify(vehicleRepository, times(1)).deleteById(vehicleId);
    }
}
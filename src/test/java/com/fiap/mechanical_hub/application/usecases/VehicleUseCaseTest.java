package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.vehicle.InsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.infrastructure.http.mappers.VehicleHttpMapper;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleUseCaseTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Spy
    private VehicleHttpMapper vehicleHttpMapper;

    @InjectMocks
    private VehicleUseCase vehicleUseCase;

    @Test
    @DisplayName("Deve criar um veículo com sucesso")
    void shouldCreateVehicleSuccessfully() {
        UUID customerId = UUID.randomUUID();
        InsertVehicleRequest request = new InsertVehicleRequest("ABC1234", "Fiat", "Uno", 2010, "Escada");

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
        InsertVehicleRequest request = new InsertVehicleRequest("ABC1234", "Fiat", "Uno", 2010, "Prata");

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleUseCase.create(customerId, request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a placa for inválida")
    void shouldThrowExceptionForInvalidPlate() {
        UUID customerId = UUID.randomUUID();
        InsertVehicleRequest request = new InsertVehicleRequest("PLACA-INVALIDA", "Ford", "Ka", 2020, "Preto");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(new Customer()));

        assertThatThrownBy(() -> vehicleUseCase.create(customerId, request))
                .isInstanceOf(InvalidLicensePlateException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção para placa duplicada na criação")
    void shouldThrowExceptionForDuplicatePlateOnCreate() {
        UUID customerId = UUID.randomUUID();
        InsertVehicleRequest request = new InsertVehicleRequest("ABC1234", "Fiat", "Uno", 2010, "Prata");

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

        InsertVehicleRequest updateRequest = new InsertVehicleRequest("NEW1234", "Toyota", "Corolla", 2022, "Branco");

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

    @Test
    @DisplayName("Deve retornar VehicleResponse quando encontrar um veículo pelo ID")
    void findById_Success() {
        UUID vehicleId = UUID.randomUUID();
        Vehicle vehicle = mock(Vehicle.class);
        VehicleResponse response = new VehicleResponse();

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(vehicleHttpMapper.toResponse(vehicle)).thenReturn(response);

        VehicleResponse result = vehicleUseCase.findById(vehicleId);

        assertNotNull(result);
        assertEquals(response, result);
        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(vehicleHttpMapper, times(1)).toResponse(vehicle);
    }

    @Test
    @DisplayName("Deve lançar NoSuchElementException quando o ID do veículo não existir")
    void findById_NotFound() {
        UUID vehicleId = UUID.randomUUID();
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            vehicleUseCase.findById(vehicleId);
        });

        assertEquals("Veículo não encontrado para o id: " + vehicleId, exception.getMessage());
        verify(vehicleRepository, times(1)).findById(vehicleId);
        verifyNoInteractions(vehicleHttpMapper);
    }

    @Test
    @DisplayName("Deve retornar uma lista de VehicleResponse no findAll")
    void findAll_Success() {
        Vehicle v1 = mock(Vehicle.class);
        Vehicle v2 = mock(Vehicle.class);
        List<Vehicle> vehicles = List.of(v1, v2);

        VehicleResponse r1 = new VehicleResponse();
        VehicleResponse r2 = new VehicleResponse();

        when(vehicleRepository.findAll()).thenReturn(vehicles);
        when(vehicleHttpMapper.toResponse(v1)).thenReturn(r1);
        when(vehicleHttpMapper.toResponse(v2)).thenReturn(r2);

        List<VehicleResponse> result = vehicleUseCase.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(r1));
        assertTrue(result.contains(r2));

        verify(vehicleRepository, times(1)).findAll();
        verify(vehicleHttpMapper, times(2)).toResponse(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia no findAll quando não houver veículos")
    void findAll_Empty() {
        when(vehicleRepository.findAll()).thenReturn(List.of());

        List<VehicleResponse> result = vehicleUseCase.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleRepository, times(1)).findAll();
        verifyNoInteractions(vehicleHttpMapper);
    }

    @Test
    @DisplayName("Deve lançar DuplicateLicensePlateException quando a nova placa já pertencer a outro veículo")
    void update_ShouldThrowException_WhenLicensePlateAlreadyExistsForAnotherVehicle() {
        UUID vehicleId = UUID.randomUUID();
        String plate = "ABC1D23";
        String normalizedPlate = "ABC1D23";

        InsertVehicleRequest request = new InsertVehicleRequest();
        request.setLicensePlate(plate);

        Vehicle existingVehicle = mock(Vehicle.class);

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));

        when(vehicleRepository.existsByLicensePlateAndIdNot(normalizedPlate, vehicleId))
                .thenReturn(true);

        DuplicateLicensePlateException exception = assertThrows(DuplicateLicensePlateException.class, () -> {
            vehicleUseCase.update(vehicleId, request);
        });

        assertEquals(String.format("Veículo com placa %s já existe", normalizedPlate), exception.getMessage());

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Deve lançar NoSuchElementException ao tentar deletar um veículo inexistente")
    void delete_ShouldThrowException_WhenVehicleDoesNotExist() {
        UUID vehicleId = UUID.randomUUID();

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            vehicleUseCase.delete(vehicleId);
        });

        String expectedMessage = VehicleUseCase.VEICULO_NAO_ENCONTRADO_PARA_O_ID + vehicleId;
        assertEquals(expectedMessage, exception.getMessage());

        verify(vehicleRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve retornar veículo existente quando a placa já estiver cadastrada")
    void findByLicensePlateOrCreate_ShouldReturnExistingVehicle() {
        UUID customerId = UUID.randomUUID();
        String plate = "BRA2E24";
        String normalizedPlate = "BRA2E24";

        Vehicle existingVehicle = Vehicle.create(
                customerId,
                normalizedPlate,
                "Toyota",
                "Corolla",
                2023,
                "Preto"
        );

        when(vehicleRepository.findByLicensePlate(normalizedPlate))
                .thenReturn(Optional.of(existingVehicle));

        Vehicle result = vehicleUseCase.findByLicensePlateOrCreate(
                customerId, plate, "Toyota", "Corolla", 2023, "Preto"
        );

        assertNotNull(result);
        assertEquals(normalizedPlate, result.getLicensePlate());
        assertEquals(existingVehicle, result);

        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(vehicleRepository, times(1)).findByLicensePlate(normalizedPlate);
    }
}
package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.infrastructure.database.models.CustomerModel;
import com.fiap.mechanical_hub.infrastructure.database.models.VehicleModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.VehicleJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleRepositoryAdapter")
class VehicleRepositoryAdapterTest {

    @Mock
    private VehicleJpaRepository jpaRepository;

    @InjectMocks
    private VehicleRepositoryAdapter repositoryAdapter;

    private UUID vehicleId;
    private UUID customerId;

    private Vehicle vehicle;
    private VehicleModel vehicleModel;
    private CustomerModel customerModel;

    @BeforeEach
    void setUp() {
        vehicleId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        customerModel = new CustomerModel();
        customerModel.setId(customerId);

        vehicle = new Vehicle(
                vehicleId,
                customerId,
                "ABC1234",
                "Toyota",
                "Corolla",
                2022,
                "Preto",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        vehicleModel = new VehicleModel(
                vehicleId,
                customerModel,
                "ABC1234",
                "Toyota",
                "Corolla",
                2022,
                "Preto",
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }

    @Test
    @DisplayName("save should persist and return mapped domain")
    void saveShouldPersistAndReturnMappedDomain() {
        when(jpaRepository.save(org.mockito.ArgumentMatchers.any(VehicleModel.class)))
                .thenReturn(vehicleModel);

        Vehicle result = repositoryAdapter.save(vehicle);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(vehicleId);
        verify(jpaRepository).save(org.mockito.ArgumentMatchers.any(VehicleModel.class));
    }

    @Test
    @DisplayName("findById should return mapped domain when found")
    void findByIdShouldReturnMappedDomain() {
        when(jpaRepository.findById(vehicleId)).thenReturn(Optional.of(vehicleModel));

        Optional<Vehicle> result = repositoryAdapter.findById(vehicleId);

        assertThat(result).isPresent();
        verify(jpaRepository).findById(vehicleId);
    }

    @Test
    @DisplayName("findById should return empty when not found")
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findById(vehicleId)).thenReturn(Optional.empty());

        Optional<Vehicle> result = repositoryAdapter.findById(vehicleId);

        assertThat(result).isEmpty();
        verify(jpaRepository).findById(vehicleId);
    }

    @Test
    @DisplayName("findByLicensePlate should return mapped domain when found")
    void findByLicensePlateShouldReturnMappedDomain() {
        when(jpaRepository.findByLicensePlate("ABC1234"))
                .thenReturn(Optional.of(vehicleModel));

        Optional<Vehicle> result = repositoryAdapter.findByLicensePlate("ABC1234");

        assertThat(result).isPresent();
        verify(jpaRepository).findByLicensePlate("ABC1234");
    }

    @Test
    @DisplayName("findByLicensePlate should return empty when not found")
    void findByLicensePlateShouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findByLicensePlate("ABC1234"))
                .thenReturn(Optional.empty());

        Optional<Vehicle> result = repositoryAdapter.findByLicensePlate("ABC1234");

        assertThat(result).isEmpty();
        verify(jpaRepository).findByLicensePlate("ABC1234");
    }

    @Test
    @DisplayName("findAll should map all entities")
    void findAllShouldMapAllEntities() {
        when(jpaRepository.findAll()).thenReturn(List.of(vehicleModel));

        List<Vehicle> result = repositoryAdapter.findAll();

        assertThat(result).hasSize(1);
        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("deleteById should delegate to JPA repository")
    void deleteByIdShouldDelegateToJpaRepository() {
        repositoryAdapter.deleteById(vehicleId);

        verify(jpaRepository).deleteById(vehicleId);
    }

    @Test
    @DisplayName("existsByLicensePlate should return JPA result")
    void existsByLicensePlateShouldReturnJpaResult() {
        when(jpaRepository.existsByLicensePlate("ABC1234")).thenReturn(true);

        boolean result = repositoryAdapter.existsByLicensePlate("ABC1234");

        assertThat(result).isTrue();
        verify(jpaRepository).existsByLicensePlate("ABC1234");
    }

    @Test
    @DisplayName("existsByLicensePlateAndIdNot should return JPA result")
    void existsByLicensePlateAndIdNotShouldReturnJpaResult() {
        when(jpaRepository.existsByLicensePlateAndIdNot("ABC1234", vehicleId))
                .thenReturn(true);

        boolean result = repositoryAdapter.existsByLicensePlateAndIdNot("ABC1234", vehicleId);

        assertThat(result).isTrue();
        verify(jpaRepository).existsByLicensePlateAndIdNot("ABC1234", vehicleId);
    }
}

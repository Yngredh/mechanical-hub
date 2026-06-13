package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.CustomerModelMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.VehicleModelMock;
import com.fiap.mechanical_hub.infrastructure.database.repositories.VehicleJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VehicleRepositoryAdapterTest {

    private final VehicleJpaRepository jpaRepository = mock(VehicleJpaRepository.class);

    private final VehicleRepositoryAdapter adapter = new VehicleRepositoryAdapter(jpaRepository);

    @Test
    void shouldReturnSavedVehicle_whenSavingVehicle() {
        when(jpaRepository.save(any())).thenReturn(VehicleModelMock.withDefaultValues());

        Vehicle result = adapter.save(VehicleMock.withDefaultValues());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(VehicleModelMock.VEHICLE_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenSavingVehicle() {
        when(jpaRepository.save(any())).thenReturn(VehicleModelMock.withDefaultValues());

        adapter.save(VehicleMock.withDefaultValues());

        verify(jpaRepository).save(any());
    }

    @Test
    void shouldReturnVehicle_whenFindByIdAndVehicleExists() {
        when(jpaRepository.findById(VehicleModelMock.VEHICLE_ID))
                .thenReturn(Optional.of(VehicleModelMock.withDefaultValues()));

        Optional<Vehicle> result = adapter.findById(VehicleModelMock.VEHICLE_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(VehicleModelMock.VEHICLE_ID);
    }

    @Test
    void shouldReturnEmpty_whenFindByIdAndVehicleDoesNotExist() {
        when(jpaRepository.findById(VehicleModelMock.VEHICLE_ID)).thenReturn(Optional.empty());

        Optional<Vehicle> result = adapter.findById(VehicleModelMock.VEHICLE_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnVehicle_whenFindByLicensePlateAndExists() {
        when(jpaRepository.findByLicensePlate("ABC1234"))
                .thenReturn(Optional.of(VehicleModelMock.withDefaultValues()));

        Optional<Vehicle> result = adapter.findByLicensePlate("ABC1234");

        assertThat(result).isPresent();
        assertThat(result.get().getLicensePlate().getValue()).isEqualTo("ABC1234");
    }

    @Test
    void shouldReturnEmpty_whenFindByLicensePlateAndNotExists() {
        when(jpaRepository.findByLicensePlate("XYZ9999")).thenReturn(Optional.empty());

        Optional<Vehicle> result = adapter.findByLicensePlate("XYZ9999");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnAllVehicles_whenFindAll() {
        when(jpaRepository.findAll()).thenReturn(List.of(VehicleModelMock.withDefaultValues()));

        List<Vehicle> result = adapter.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnTrue_whenLicensePlateExists() {
        when(jpaRepository.existsByLicensePlate("ABC1234")).thenReturn(true);

        boolean result = adapter.existsByLicensePlate("ABC1234");

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnVehiclesByCustomer_whenFindAllVehiclesByCustomerId() {
        when(jpaRepository.findByCustomerId(CustomerModelMock.CUSTOMER_ID))
                .thenReturn(List.of(VehicleModelMock.withDefaultValues()));

        List<Vehicle> result = adapter.findAllVehiclesByCustomerId(CustomerModelMock.CUSTOMER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(CustomerModelMock.CUSTOMER_ID);
    }
}

package com.fiap.mechanical_hub.domain.service;

import com.fiap.mechanical_hub.domain.exceptions.DuplicateLicensePlateException;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.domain.valueobjects.LicensePlate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VehicleDomainServiceTest {

    private final VehicleRepository vehicleRepository = mock(VehicleRepository.class);
    private final VehicleDomainService service = new VehicleDomainService(vehicleRepository);

    @Test
    void shouldCreateLicensePlate_whenPlateIsUnique() {
        String plateValue = "ABC1234";
        when(vehicleRepository.existsByLicensePlate(plateValue)).thenReturn(false);

        LicensePlate licensePlate = service.createLicensePlate(plateValue);

        assertThat(licensePlate).isNotNull();
        assertThat(licensePlate.getValue()).isNotNull();
    }

    @Test
    void shouldThrowException_whenLicensePlateAlreadyExists() {
        String plateValue = "ABC1234";
        when(vehicleRepository.existsByLicensePlate(plateValue)).thenReturn(true);

        assertThatThrownBy(() -> service.createLicensePlate(plateValue))
                .isInstanceOf(DuplicateLicensePlateException.class)
                .hasMessageContaining("ABC1234");
    }

    @Test
    void shouldValidateUniqueLicensePlate_whenNotExists() {
        LicensePlate licensePlate = new LicensePlate("XYZ9999");
        when(vehicleRepository.existsByLicensePlate("XYZ9999")).thenReturn(false);

        assertThatCode(() -> service.validateUniqueLicensePlate(licensePlate))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_whenValidatingDuplicatePlate() {
        LicensePlate licensePlate = new LicensePlate("XYZ9999");
        when(vehicleRepository.existsByLicensePlate("XYZ9999")).thenReturn(true);

        assertThatThrownBy(() -> service.validateUniqueLicensePlate(licensePlate))
                .isInstanceOf(DuplicateLicensePlateException.class);
    }

    @Test
    void shouldCreateOldStandardPlate_whenUnique() {
        String plateValue = "OLD1234";
        when(vehicleRepository.existsByLicensePlate(plateValue)).thenReturn(false);

        LicensePlate licensePlate = service.createLicensePlate(plateValue);

        assertThat(licensePlate.getValue()).isNotNull();
    }

    @Test
    void shouldCreateMercosulPlate_whenUnique() {
        String plateValue = "NEW1D34";
        when(vehicleRepository.existsByLicensePlate(plateValue)).thenReturn(false);

        LicensePlate licensePlate = service.createLicensePlate(plateValue);

        assertThat(licensePlate.getValue()).isNotNull();
    }

    @Test
    void shouldThrowException_whenCreatingDuplicateOldStandardPlate() {
        String plateValue = "OLD1234";
        when(vehicleRepository.existsByLicensePlate(plateValue)).thenReturn(true);

        assertThatThrownBy(() -> service.createLicensePlate(plateValue))
                .isInstanceOf(DuplicateLicensePlateException.class);
    }

    @Test
    void shouldThrowException_whenCreatingDuplicateMercosulPlate() {
        String plateValue = "NEW1D34";
        when(vehicleRepository.existsByLicensePlate(plateValue)).thenReturn(true);

        assertThatThrownBy(() -> service.createLicensePlate(plateValue))
                .isInstanceOf(DuplicateLicensePlateException.class);
    }

    @Test
    void shouldValidateMultipleDifferentPlates() {
        LicensePlate plate1 = new LicensePlate("ABC1111");
        LicensePlate plate2 = new LicensePlate("XYZ9999");

        when(vehicleRepository.existsByLicensePlate("ABC1111")).thenReturn(false);
        when(vehicleRepository.existsByLicensePlate("XYZ9999")).thenReturn(false);

        assertThatCode(() -> {
            service.validateUniqueLicensePlate(plate1);
            service.validateUniqueLicensePlate(plate2);
        }).doesNotThrowAnyException();
    }

    @Test
    void shouldIncludeCorrectErrorMessage_whenDuplicatePlateException() {
        String plateValue = "DUP1234";
        when(vehicleRepository.existsByLicensePlate(plateValue)).thenReturn(true);

        assertThatThrownBy(() -> service.createLicensePlate(plateValue))
                .isInstanceOf(DuplicateLicensePlateException.class)
                .hasMessageContaining(plateValue)
                .hasMessageContaining("já existe");
    }
}
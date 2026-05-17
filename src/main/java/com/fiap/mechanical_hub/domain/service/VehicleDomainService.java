package com.fiap.mechanical_hub.domain.service;

import com.fiap.mechanical_hub.domain.exceptions.DuplicateLicensePlateException;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.domain.valueobjects.LicensePlate;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class VehicleDomainService {

    private final VehicleRepository repository;

    public LicensePlate createLicensePlate(String value) {
        LicensePlate licensePlate = new LicensePlate(value);
        validateUniqueLicensePlate(licensePlate);
        return licensePlate;
    }

    public void validateUniqueLicensePlate(LicensePlate licensePlate) {
        if (repository.existsByLicensePlate(licensePlate.getValue())) {
            throw new DuplicateLicensePlateException(
                    String.format("Veículo com placa %s já existe", licensePlate.getValue())
            );
        }
    }

}


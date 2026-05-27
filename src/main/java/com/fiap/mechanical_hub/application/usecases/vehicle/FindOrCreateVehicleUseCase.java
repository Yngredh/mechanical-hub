package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.domain.service.VehicleDomainService;
import com.fiap.mechanical_hub.domain.valueobjects.LicensePlate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindOrCreateVehicleUseCase {

    private final VehicleRepository vehicleRepository;
    private final VehicleDomainService vehicleDomainService;

    @Transactional
    public Vehicle execute(UUID customerId, String licensePlate, String brand, String model, Integer year, String color) {
        LicensePlate plate = new LicensePlate(licensePlate);
        
        Optional<Vehicle> existingVehicle = vehicleRepository.findByLicensePlate(plate.getValue());

        if (existingVehicle.isPresent()) return existingVehicle.get();

        LicensePlate newLicensePlate = vehicleDomainService.createLicensePlate(licensePlate);

        Vehicle newVehicle = Vehicle.create(
                customerId,
                newLicensePlate,
                brand,
                model,
                year,
                color
        );

        return vehicleRepository.save(newVehicle);
    }
}


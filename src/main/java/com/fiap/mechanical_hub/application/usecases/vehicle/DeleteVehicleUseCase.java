package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteVehicleUseCase {

    private final VehicleRepository vehicleRepository;

    @Transactional
    public void execute(UUID customerId) {
        log.info("Deleting vehicle from customerId: {}", customerId);

        List<Vehicle> vehicles = vehicleRepository.findAllVehiclesByCustomerId(customerId);

        if (vehicles.isEmpty()) {
            log.warn("No vehicles found for customer with id: {}", customerId);
            return;
        }

        for (Vehicle vehicle : vehicles) {
            vehicle.deactivate();
            vehicleRepository.save(vehicle);

            log.info("Vehicle with id: {} inactivated successfully", vehicle.getId());
        }

    }
}


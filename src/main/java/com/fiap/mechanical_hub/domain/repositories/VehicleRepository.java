package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.Vehicle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository {

    Vehicle save(Vehicle vehicle);

    Optional<Vehicle> findById(UUID id);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findAll();

    void deleteById(UUID id);

    boolean existsByLicensePlate(String licensePlate);

}


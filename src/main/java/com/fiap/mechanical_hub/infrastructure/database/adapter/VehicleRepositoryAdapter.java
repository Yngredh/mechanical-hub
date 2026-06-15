package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.infrastructure.database.mappers.VehicleRepositoryMapper;
import com.fiap.mechanical_hub.infrastructure.database.models.VehicleModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.VehicleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.mechanical_hub.infrastructure.database.mappers.VehicleRepositoryMapper.toDomainEntity;
import static com.fiap.mechanical_hub.infrastructure.database.mappers.VehicleRepositoryMapper.toJpaEntity;

@Component
@RequiredArgsConstructor
public class VehicleRepositoryAdapter implements VehicleRepository {

    private final VehicleJpaRepository jpaRepository;

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleModel entity = toJpaEntity(vehicle);
        VehicleModel saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return jpaRepository.findById(id).map(VehicleRepositoryMapper::toDomainEntity);
    }

    @Override
    public Optional<Vehicle> findByLicensePlate(String licensePlate) {
        return jpaRepository.findByLicensePlate(licensePlate).map(VehicleRepositoryMapper::toDomainEntity);
    }

    @Override
    public List<Vehicle> findAll() {
        return jpaRepository.findAll().stream()
                .map(VehicleRepositoryMapper::toDomainEntity)
                .toList();
    }

    @Override
    public boolean existsByLicensePlate(String licensePlate) {
        return jpaRepository.existsByLicensePlate(licensePlate);
    }

    @Override
    public List<Vehicle> findAllVehiclesByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId).stream()
                .map(VehicleRepositoryMapper::toDomainEntity)
                .toList();
    }

}


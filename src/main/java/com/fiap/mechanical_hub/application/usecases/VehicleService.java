package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.vehicle.UpsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.mappers.VehicleMapper;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.exceptions.DuplicateLicensePlateException;
import com.fiap.mechanical_hub.application.repositories.CustomerRepository;
import com.fiap.mechanical_hub.application.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.mechanical_hub.shared.utils.license_plate.LicensePlateFormatter.normalize;
import static com.fiap.mechanical_hub.shared.utils.license_plate.LicensePlateValidator.validateLicensePlate;


@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleResponse create(UUID customerId, UpsertVehicleRequest request) {
        if (customerRepository.findById(customerId).isEmpty()) {
            throw new NoSuchElementException("Cliente não encontrado para o id: " + customerId);
        }

        validateLicensePlate(request.getLicensePlate());
        String normalizedPlate = normalize(request.getLicensePlate());



        if (vehicleRepository.existsByLicensePlate(normalizedPlate)) {
            throw new DuplicateLicensePlateException(
                    String.format("Veículo com placa %s já existe", normalizedPlate)
            );
        }

        Vehicle vehicle = vehicleMapper.toDomainEntity(customerId, request);
        Vehicle saved = vehicleRepository.save(vehicle);

        return vehicleMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public VehicleResponse findById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Veículo não encontrado para o id: " + id));
        return vehicleMapper.toResponse(vehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    public VehicleResponse update(UUID id, UpsertVehicleRequest request) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Veículo não encontrado para o id: " + id));

        validateLicensePlate(request.getLicensePlate());
        String normalizedPlate = normalize(request.getLicensePlate());

        if (vehicleRepository.existsByLicensePlateAndIdNot(normalizedPlate, id)) {
            throw new DuplicateLicensePlateException(
                    String.format("Veículo com placa %s já existe", normalizedPlate)
            );
        }

        Vehicle updatedVehicle = vehicleMapper.toDomainEntity(request, existingVehicle);
        Vehicle saved = vehicleRepository.save(updatedVehicle);
        return vehicleMapper.toResponse(saved);
    }

    public void delete(UUID id) {
        if (vehicleRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Veículo não encontrado para o id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public Vehicle findByLicensePlateOrCreate(UUID customerId, String licensePlate, String brand,
                                               String model, Integer year, String color) {
        validateLicensePlate(licensePlate);
        String normalizedPlate = normalize(licensePlate);

        Optional<Vehicle> existingVehicle = vehicleRepository.findByLicensePlate(normalizedPlate);

        if (existingVehicle.isPresent()) {
            return existingVehicle.get();
        }

        Vehicle newVehicle = Vehicle.create(customerId, normalizedPlate, brand, model, year, color);
        return vehicleRepository.save(newVehicle);
    }

}

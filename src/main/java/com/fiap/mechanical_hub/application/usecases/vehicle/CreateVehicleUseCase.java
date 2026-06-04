package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.application.command.vehicle.CreateVehicleCommand;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.domain.service.VehicleDomainService;
import com.fiap.mechanical_hub.domain.valueobjects.LicensePlate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fiap.mechanical_hub.application.mappers.VehicleMapper.toVehicleResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateVehicleUseCase {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleDomainService vehicleDomainService;

    @Transactional
    public VehicleResponse execute(CreateVehicleCommand command) {
        log.info("Creating vehicle for customer: {}", command.customerId());

        if (customerRepository.findById(command.customerId()).isEmpty()) {
            throw new NotFoundException("Cliente não encontrado para o id: " + command.customerId());
        }

        LicensePlate licensePlate = vehicleDomainService.createLicensePlate(command.licensePlate());

        Vehicle vehicle = Vehicle.create(
                command.customerId(),
                licensePlate,
                command.brand(),
                command.model(),
                command.year(),
                command.color()
        );

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created successfully with id: {}", savedVehicle.getId());

        return toVehicleResponse(savedVehicle);
    }
}


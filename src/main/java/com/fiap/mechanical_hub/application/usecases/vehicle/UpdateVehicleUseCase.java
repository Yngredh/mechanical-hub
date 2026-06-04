package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.application.command.vehicle.UpdateVehicleCommand;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fiap.mechanical_hub.application.mappers.VehicleMapper.toVehicleResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateVehicleUseCase {

    private final VehicleRepository vehicleRepository;

    @Transactional
    public VehicleResponse execute(UpdateVehicleCommand command) {
        log.info("Updating vehicle with id: {}", command.id());

        Vehicle existingVehicle = vehicleRepository.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado para o id: " + command.id()));

        existingVehicle.update(
                command.brand(),
                command.model(),
                command.year(),
                command.color()
        );

        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
        log.info("Vehicle with id: {} updated successfully", command.id());

        return toVehicleResponse(updatedVehicle);
    }
}


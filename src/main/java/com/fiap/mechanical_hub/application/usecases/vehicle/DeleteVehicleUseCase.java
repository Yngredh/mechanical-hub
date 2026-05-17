package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteVehicleUseCase {

    private final VehicleRepository vehicleRepository;

    @Transactional
    public void execute(UUID id) {
        log.info("Deleting vehicle with id: {}", id);

        if (vehicleRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Veículo não encontrado para o id: " + id);
        }

        vehicleRepository.deleteById(id);
        log.info("Vehicle with id: {} deleted successfully", id);
    }
}


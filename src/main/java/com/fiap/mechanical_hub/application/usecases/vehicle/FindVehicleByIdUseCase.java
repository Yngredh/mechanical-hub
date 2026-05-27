package com.fiap.mechanical_hub.application.usecases.vehicle;

import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.fiap.mechanical_hub.infrastructure.http.mappers.VehicleHttpMapper.toVehicleResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindVehicleByIdUseCase {

    private final VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public VehicleResponse execute(UUID id) {
        log.info("Retrieving vehicle with id: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado para o id: " + id));

        return toVehicleResponse(vehicle);
    }
}


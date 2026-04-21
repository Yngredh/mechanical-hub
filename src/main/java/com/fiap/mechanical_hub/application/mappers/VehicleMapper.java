package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.vehicle.UpsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VehicleMapper {

    public Vehicle toDomainEntity(UUID customerId, UpsertVehicleRequest request) {
        return Vehicle.create(
                customerId,
                request.getLicensePlate(),
                request.getBrand(),
                request.getModel(),
                request.getYear(),
                request.getColor()
        );
    }

    public Vehicle toDomainEntity(UpsertVehicleRequest request, Vehicle existingVehicle) {
        existingVehicle.update(
                request.getLicensePlate(),
                request.getBrand(),
                request.getModel(),
                request.getYear(),
                request.getColor()
        );
        return existingVehicle;
    }

    public VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getCustomerId(),
                vehicle.getLicensePlate(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getColor(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }
}


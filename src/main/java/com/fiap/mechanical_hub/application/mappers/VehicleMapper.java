package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.command.vehicle.CreateVehicleCommand;
import com.fiap.mechanical_hub.application.command.vehicle.UpdateVehicleCommand;
import com.fiap.mechanical_hub.application.dto.vehicle.InsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.UpdateVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VehicleMapper {

    public static CreateVehicleCommand toCreateVehicleCommand(InsertVehicleRequest request, UUID customerId) {
        return new CreateVehicleCommand(
                customerId,
                request.getLicensePlate(),
                request.getBrand(),
                request.getModel(),
                request.getYear(),
                request.getColor()
        );
    }

    public static UpdateVehicleCommand toUpdateVehicleCommand(UUID id, UpdateVehicleRequest request) {
        return new UpdateVehicleCommand(
                id,
                request.getBrand(),
                request.getModel(),
                request.getYear(),
                request.getColor()
        );
    }

    public static VehicleResponse toVehicleResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getCustomerId(),
                vehicle.getLicensePlate().getValue(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getColor(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }

}

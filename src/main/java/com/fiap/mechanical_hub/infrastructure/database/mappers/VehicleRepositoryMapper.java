package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.valueobjects.LicensePlate;
import com.fiap.mechanical_hub.infrastructure.database.models.CustomerModel;
import com.fiap.mechanical_hub.infrastructure.database.models.VehicleModel;

public class VehicleRepositoryMapper {

    private VehicleRepositoryMapper() {}


    public static VehicleModel toJpaEntity(Vehicle vehicle) {
        CustomerModel customerRef = new CustomerModel();
        customerRef.setId(vehicle.getCustomerId());

        return new VehicleModel(
                vehicle.getId(),
                customerRef,
                vehicle.getLicensePlate().getValue(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getColor(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt(),
                vehicle.getDeletedAt()
        );
    }

    public static Vehicle toDomainEntity(VehicleModel entity) {
        return new Vehicle(
                entity.getId(),
                entity.getCustomer().getId(),
                new LicensePlate(entity.getLicensePlate()),
                entity.getBrand(),
                entity.getModel(),
                entity.getYear(),
                entity.getColor(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

}

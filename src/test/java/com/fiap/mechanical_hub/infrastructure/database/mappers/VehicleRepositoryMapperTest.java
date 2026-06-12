package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.infrastructure.database.models.CustomerModel;
import com.fiap.mechanical_hub.infrastructure.database.models.VehicleModel;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.CustomerModelMock;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleRepositoryMapperTest {

    private VehicleModel buildModel(Vehicle vehicle) {
        CustomerModel customerRef = new CustomerModel();
        customerRef.setId(CustomerModelMock.CUSTOMER_ID);

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

    @Test
    void shouldMapAllFields_whenConvertingDomainToJpaEntity() {
        Vehicle vehicle = VehicleMock.withDefaultValues();

        VehicleModel model = VehicleRepositoryMapper.toJpaEntity(vehicle);

        assertThat(model.getId()).isEqualTo(vehicle.getId());
        assertThat(model.getLicensePlate()).isEqualTo(vehicle.getLicensePlate().getValue());
        assertThat(model.getBrand()).isEqualTo(vehicle.getBrand());
        assertThat(model.getModel()).isEqualTo(vehicle.getModel());
        assertThat(model.getYear()).isEqualTo(vehicle.getYear());
        assertThat(model.getColor()).isEqualTo(vehicle.getColor());
    }

    @Test
    void shouldSetCustomerRefWithVehicleCustomerId_whenConvertingDomainToJpaEntity() {
        Vehicle vehicle = VehicleMock.withDefaultValues();

        VehicleModel model = VehicleRepositoryMapper.toJpaEntity(vehicle);

        assertThat(model.getCustomer().getId()).isEqualTo(vehicle.getCustomerId());
    }

    @Test
    void shouldMapAllFields_whenConvertingJpaEntityToDomainEntity() {
        Vehicle vehicle = VehicleMock.withDefaultValues();
        VehicleModel model = buildModel(vehicle);

        Vehicle domain = VehicleRepositoryMapper.toDomainEntity(model);

        assertThat(domain.getId()).isEqualTo(model.getId());
        assertThat(domain.getCustomerId()).isEqualTo(model.getCustomer().getId());
        assertThat(domain.getLicensePlate().getValue()).isEqualTo(model.getLicensePlate());
        assertThat(domain.getBrand()).isEqualTo(model.getBrand());
        assertThat(domain.getModel()).isEqualTo(model.getModel());
        assertThat(domain.getYear()).isEqualTo(model.getYear());
        assertThat(domain.getColor()).isEqualTo(model.getColor());
    }

    @Test
    void shouldPreserveDeletedAt_whenConvertingInactiveVehicle() {
        Vehicle vehicle = VehicleMock.inactive();
        VehicleModel model = buildModel(vehicle);

        Vehicle domain = VehicleRepositoryMapper.toDomainEntity(model);

        assertThat(domain.getDeletedAt()).isNotNull();
    }
}

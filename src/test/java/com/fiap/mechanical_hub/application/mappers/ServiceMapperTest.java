package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.command.ordertask.CreateServiceCommand;
import com.fiap.mechanical_hub.application.command.ordertask.UpdateServiceCommand;
import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.application.dto.service.UpsertServiceRequest;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.mocks.application.dto.UpsertServiceRequestMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceDataMock;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceMapperTest {

    private static final UUID SERVICE_ID = UUID.fromString("00000000-0000-0000-0000-000000000040");

    @Test
    void shouldMapAllFields_whenConvertingServiceDataToResponse() {
        ServiceData serviceData = ServiceDataMock.withDefaultValues();

        ServiceResponse response = ServiceMapper.toResponse(serviceData);

        assertThat(response.getId()).isEqualTo(serviceData.getId());
        assertThat(response.getName()).isEqualTo(serviceData.getName());
        assertThat(response.getDescription()).isEqualTo(serviceData.getDescription());
        assertThat(response.getLaborCost()).isEqualTo(serviceData.getLaborCost());
        assertThat(response.getBasePrice()).isEqualTo(serviceData.getBasePrice());
        assertThat(response.getTotalPrice()).isEqualTo(serviceData.getTotalPrice());
        assertThat(response.isActive()).isEqualTo(serviceData.isActive());
    }

    @Test
    void shouldMapMaterials_whenServiceHasMaterials() {
        ServiceData serviceData = ServiceDataMock.withDefaultValues();

        ServiceResponse response = ServiceMapper.toResponse(serviceData);

        assertThat(response.getMaterials()).hasSize(serviceData.getMaterials().size());
        assertThat(response.getMaterials().getFirst().getMaterialName())
                .isEqualTo(serviceData.getMaterials().getFirst().getMaterial().getName());
    }

    @Test
    void shouldMapAllFields_whenConvertingRequestToCreateCommand() {
        UpsertServiceRequest request = UpsertServiceRequestMock.withDefaultValues();

        CreateServiceCommand command = ServiceMapper.toCreateCommand(request);

        assertThat(command.name()).isEqualTo(request.getName());
        assertThat(command.description()).isEqualTo(request.getDescription());
        assertThat(command.laborCost()).isEqualTo(request.getLaborCost());
        assertThat(command.basePrice()).isEqualTo(request.getBasePrice());
        assertThat(command.materials()).isEqualTo(request.getMaterials());
    }

    @Test
    void shouldMapAllFields_whenConvertingRequestToUpdateCommand() {
        UpsertServiceRequest request = UpsertServiceRequestMock.withDefaultValues();

        UpdateServiceCommand command = ServiceMapper.toUpdateCommand(SERVICE_ID, request);

        assertThat(command.id()).isEqualTo(SERVICE_ID);
        assertThat(command.name()).isEqualTo(request.getName());
        assertThat(command.description()).isEqualTo(request.getDescription());
        assertThat(command.laborCost()).isEqualTo(request.getLaborCost());
        assertThat(command.basePrice()).isEqualTo(request.getBasePrice());
        assertThat(command.materials()).isEqualTo(request.getMaterials());
    }
}

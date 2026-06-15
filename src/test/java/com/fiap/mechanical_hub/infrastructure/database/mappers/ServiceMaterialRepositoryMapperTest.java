package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.application.dto.servicematerials.ServiceMaterialResponse;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceMaterialMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.ServiceMaterialModelMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.ServiceModelMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceMaterialRepositoryMapperTest {

    @Test
    void shouldMapAllFields_whenConvertingDomainToJpaEntity() {
        ServiceMaterial serviceMaterial = ServiceMaterialMock.withDefaultValues();
        ServiceModel parentService = ServiceModelMock.withNoMaterials();

        ServiceMaterialModel model = ServiceMaterialRepositoryMapper.toJpaEntity(serviceMaterial, parentService);

        assertThat(model.getId()).isEqualTo(serviceMaterial.getId());
        assertThat(model.getQuantity()).isEqualTo(serviceMaterial.getQuantity());
        assertThat(model.getService()).isEqualTo(parentService);
        assertThat(model.getMaterial().getId()).isEqualTo(serviceMaterial.getMaterial().getId());
    }

    @Test
    void shouldMapAllFields_whenConvertingJpaEntityToDomainEntity() {
        ServiceMaterialModel model = ServiceMaterialModelMock.withDefaultValues();

        ServiceMaterial serviceMaterial = ServiceMaterialRepositoryMapper.toDomainEntity(model);

        assertThat(serviceMaterial.getId()).isEqualTo(model.getId());
        assertThat(serviceMaterial.getServiceId()).isEqualTo(model.getService().getId());
        assertThat(serviceMaterial.getQuantity()).isEqualTo(model.getQuantity());
        assertThat(serviceMaterial.getMaterial().getId()).isEqualTo(model.getMaterial().getId());
        assertThat(serviceMaterial.getMaterial().getName()).isEqualTo(model.getMaterial().getName());
        assertThat(serviceMaterial.getMaterial().getUnitPrice()).isEqualTo(model.getMaterial().getUnitPrice());
    }

    @Test
    void shouldMapMaterialFields_whenConvertingToResponse() {
        ServiceMaterial serviceMaterial = ServiceMaterialMock.withDefaultValues();

        ServiceMaterialResponse response = ServiceMaterialRepositoryMapper.toResponse(serviceMaterial);

        assertThat(response.getMaterialName()).isEqualTo(serviceMaterial.getMaterial().getName());
        assertThat(response.getMaterialDescription()).isEqualTo(serviceMaterial.getMaterial().getDescription());
        assertThat(response.getUnitPrice()).isEqualTo(serviceMaterial.getMaterial().getUnitPrice());
        assertThat(response.getQuantity()).isEqualTo(serviceMaterial.getQuantity());
    }
}

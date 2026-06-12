package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceDataMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.ServiceModelMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceRepositoryMapperTest {

    @Test
    void shouldMapAllFields_whenConvertingJpaEntityToDomainEntityWithNoMaterials() {
        ServiceModel model = ServiceModelMock.withNoMaterials();

        ServiceData serviceData = ServiceRepositoryMapper.toDomainEntity(model);

        assertThat(serviceData.getId()).isEqualTo(model.getId());
        assertThat(serviceData.getName()).isEqualTo(model.getName());
        assertThat(serviceData.getDescription()).isEqualTo(model.getDescription());
        assertThat(serviceData.getLaborCost()).isEqualTo(model.getLaborCost());
        assertThat(serviceData.getBasePrice()).isEqualTo(model.getBasePrice());
        assertThat(serviceData.getTotalPrice()).isEqualTo(model.getTotalPrice());
        assertThat(serviceData.isActive()).isEqualTo(model.isActive());
    }

    @Test
    void shouldMapMaterials_whenServiceModelHasMaterials() {
        ServiceModel model = ServiceModelMock.withOneMaterial();

        ServiceData serviceData = ServiceRepositoryMapper.toDomainEntity(model);

        assertThat(serviceData.getMaterials()).hasSize(1);
        assertThat(serviceData.getMaterials().getFirst().getMaterial().getId())
                .isEqualTo(model.getMaterials().getFirst().getMaterial().getId());
    }

    @Test
    void shouldReturnEmptyMaterialsList_whenServiceModelHasNoMaterials() {
        ServiceModel model = ServiceModelMock.withNoMaterials();

        ServiceData serviceData = ServiceRepositoryMapper.toDomainEntity(model);

        assertThat(serviceData.getMaterials()).isEmpty();
    }

    @Test
    void shouldMapAllFields_whenConvertingDomainToJpaEntity() {
        ServiceData serviceData = ServiceDataMock.withDefaultValues();

        ServiceModel model = ServiceRepositoryMapper.toJpaEntity(serviceData);

        assertThat(model.getId()).isEqualTo(serviceData.getId());
        assertThat(model.getName()).isEqualTo(serviceData.getName());
        assertThat(model.getDescription()).isEqualTo(serviceData.getDescription());
        assertThat(model.getLaborCost()).isEqualTo(serviceData.getLaborCost());
        assertThat(model.getBasePrice()).isEqualTo(serviceData.getBasePrice());
        assertThat(model.getTotalPrice()).isEqualTo(serviceData.getTotalPrice());
        assertThat(model.isActive()).isEqualTo(serviceData.isActive());
    }

    @Test
    void shouldMapServiceMaterials_whenDomainHasMaterials() {
        ServiceData serviceData = ServiceDataMock.withDefaultValues();

        ServiceModel model = ServiceRepositoryMapper.toJpaEntity(serviceData);

        assertThat(model.getMaterials()).hasSize(serviceData.getMaterials().size());
        assertThat(model.getMaterials().getFirst().getMaterial().getId())
                .isEqualTo(serviceData.getMaterials().getFirst().getMaterial().getId());
    }
}

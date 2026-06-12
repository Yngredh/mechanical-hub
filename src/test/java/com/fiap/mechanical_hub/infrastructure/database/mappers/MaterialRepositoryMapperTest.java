package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.infrastructure.database.models.MaterialModel;
import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.MaterialModelMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialRepositoryMapperTest {

    @Test
    void shouldMapAllFields_whenConvertingDomainToJpaEntity() {
        Material material = MaterialMock.withSufficientStock();

        MaterialModel model = MaterialRepositoryMapper.toJpaEntity(material);

        assertThat(model.getId()).isEqualTo(material.getId());
        assertThat(model.getName()).isEqualTo(material.getName());
        assertThat(model.getDescription()).isEqualTo(material.getDescription());
        assertThat(model.getUnitPrice()).isEqualTo(material.getUnitPrice());
        assertThat(model.getMinStockQuantity()).isEqualTo(material.getMinStockQuantity());
    }

    @Test
    void shouldMapAllFields_whenConvertingJpaEntityToDomainEntity() {
        MaterialModel model = MaterialModelMock.withDefaultValues();

        Material material = MaterialRepositoryMapper.toDomainEntity(model);

        assertThat(material.getId()).isEqualTo(model.getId());
        assertThat(material.getName()).isEqualTo(model.getName());
        assertThat(material.getDescription()).isEqualTo(model.getDescription());
        assertThat(material.getUnitPrice()).isEqualTo(model.getUnitPrice());
        assertThat(material.getMinStockQuantity()).isEqualTo(model.getMinStockQuantity());
    }

    @Test
    void shouldProduceSameResult_whenToModelDelegatesToToJpaEntity() {
        Material material = MaterialMock.withSufficientStock();

        MaterialModel fromToJpa = MaterialRepositoryMapper.toJpaEntity(material);
        MaterialModel fromToModel = MaterialRepositoryMapper.toModel(material);

        assertThat(fromToModel.getId()).isEqualTo(fromToJpa.getId());
        assertThat(fromToModel.getName()).isEqualTo(fromToJpa.getName());
        assertThat(fromToModel.getUnitPrice()).isEqualTo(fromToJpa.getUnitPrice());
    }
}

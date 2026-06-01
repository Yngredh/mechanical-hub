package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialTest {

    @Test
    void shouldCreateMaterial_withValidData() {
        Material material = Material.create(
                "Óleo de motor",
                "Óleo 5W30",
                BigDecimal.valueOf(45.00),
                10
        );

        assertThat(material.getId()).isNotNull();
        assertThat(material.getName()).isEqualTo("Óleo de motor");
        assertThat(material.getUnitPrice()).isEqualTo(BigDecimal.valueOf(45.00));
        assertThat(material.getMinStockQuantity()).isEqualTo(10);
    }

    @Test
    void shouldUpdateMaterial_withNewData() {
        Material material = MaterialMock.withSufficientStock();
        LocalDateTime originalUpdatedAt = material.getUpdatedAt();

        material.update("Novo nome", "Novo descr", BigDecimal.valueOf(55.00), 15);

        assertThat(material.getName()).isEqualTo("Novo nome");
        assertThat(material.getUnitPrice()).isEqualTo(BigDecimal.valueOf(55.00));
        assertThat(material.getMinStockQuantity()).isEqualTo(15);
        assertThat(material.getUpdatedAt()).isNotEqualTo(originalUpdatedAt);
    }

    @Test
    void shouldDeactivateMaterial_settingDeletedAt() {
        Material material = MaterialMock.withSufficientStock();

        material.deactivate();

        assertThat(material.getDeletedAt()).isNotNull();
        assertThat(material.isActive()).isFalse();
    }

    @Test
    void shouldReturnTrue_whenMaterialIsActive() {
        Material material = MaterialMock.withSufficientStock();

        assertThat(material.isActive()).isTrue();
    }

    @Test
    void shouldReturnFalse_whenMaterialIsInactive() {
        Material material = MaterialMock.withSufficientStock();
        material.deactivate();

        assertThat(material.isActive()).isFalse();
    }

    @Test
    void shouldMaintainIdAndCreatedAtAfterUpdate() {
        Material material = MaterialMock.withSufficientStock();
        var originalId = material.getId();
        var originalCreatedAt = material.getCreatedAt();

        material.update("Novo", "Desc", BigDecimal.valueOf(50.00), 5);

        assertThat(material.getId()).isEqualTo(originalId);
        assertThat(material.getCreatedAt()).isEqualTo(originalCreatedAt);
    }
}


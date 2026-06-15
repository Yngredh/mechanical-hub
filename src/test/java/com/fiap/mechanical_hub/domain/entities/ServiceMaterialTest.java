package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceMaterialMock;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceMaterialTest {

    @Test
    void shouldCreateServiceMaterial_withValidData() {
        Material material = MaterialMock.withSufficientStock();

        ServiceMaterial serviceMaterial = ServiceMaterial.create(material, 3);

        assertThat(serviceMaterial.getId()).isNotNull();
        assertThat(serviceMaterial.getMaterial()).isEqualTo(material);
        assertThat(serviceMaterial.getQuantity()).isEqualTo(3);
    }

    @Test
    void shouldThrowException_whenCreatingWithNullMaterial() {
        assertThatThrownBy(() -> ServiceMaterial.create(null, 5))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Material");
    }

    @Test
    void shouldThrowException_whenCreatingWithZeroQuantity() {
        Material material = MaterialMock.withSufficientStock();

        assertThatThrownBy(() -> ServiceMaterial.create(material, 0))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void shouldThrowException_whenCreatingWithNegativeQuantity() {
        Material material = MaterialMock.withSufficientStock();

        assertThatThrownBy(() -> ServiceMaterial.create(material, -3))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void shouldCalculateCost_correctlyWithQuantityAndUnitPrice() {
        ServiceMaterial serviceMaterial = ServiceMaterialMock.withQuantity(5);

        BigDecimal cost = serviceMaterial.calculateCost();

        assertThat(cost).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void shouldCalculateCostAsMultipleOfUnitPrice() {
        Material material = MaterialMock.withPrice(BigDecimal.valueOf(100.00));
        ServiceMaterial serviceMaterial = new ServiceMaterial(
                java.util.UUID.randomUUID(),
                java.util.UUID.randomUUID(),
                material,
                3
        );

        BigDecimal cost = serviceMaterial.calculateCost();

        assertThat(cost).isEqualTo(BigDecimal.valueOf(300.00));
    }
}


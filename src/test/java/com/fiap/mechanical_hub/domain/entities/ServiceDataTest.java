package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.mocks.domain.entities.ServiceDataMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceMaterialMock;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceDataTest {

    @Test
    void shouldCreateServiceData_withValidData() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.withDefaultValues());

        ServiceData service = ServiceData.create(
                "Troca de óleo",
                "Troca de óleo e filtro",
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(80.00),
                materials
        );

        assertThat(service.getId()).isNotNull();
        assertThat(service.getName()).isEqualTo("Troca de óleo");
        assertThat(service.isActive()).isTrue();
    }

    @Test
    void shouldThrowException_whenCreatingWithBlankName() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.withDefaultValues());
        String blankName = "";
        String description = "Description";
        BigDecimal laborCost = BigDecimal.valueOf(50.00);
        BigDecimal basePrice = BigDecimal.valueOf(80.00);

        assertThatThrownBy(() -> ServiceData.create(blankName, description, laborCost, basePrice, materials))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("name");
    }

    @Test
    void shouldThrowException_whenCreatingWithNegativeLaborCost() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.withDefaultValues());
        String name = "Service";
        String description = "Description";
        BigDecimal negativeLaborCost = BigDecimal.valueOf(-50.00);
        BigDecimal basePrice = BigDecimal.valueOf(80.00);

        assertThatThrownBy(() -> ServiceData.create(name, description, negativeLaborCost, basePrice, materials))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Labor cost");
    }

    @Test
    void shouldThrowException_whenCreatingWithNegativeBasePrice() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.withDefaultValues());
        String name = "Service";
        String description = "Description";
        BigDecimal laborCost = BigDecimal.valueOf(50.00);
        BigDecimal negativeBasePrice = BigDecimal.valueOf(-80.00);

        assertThatThrownBy(() -> ServiceData.create(name, description, laborCost, negativeBasePrice, materials))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Base price");
    }

    @Test
    void shouldThrowException_whenCreatingWithNullMaterials() {
        String name = "Service";
        String description = "Description";
        BigDecimal laborCost = BigDecimal.valueOf(50.00);
        BigDecimal basePrice = BigDecimal.valueOf(80.00);

        assertThatThrownBy(() -> ServiceData.create(name, description, laborCost, basePrice, null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Materials");
    }

    @Test
    void shouldCalculateTotalPrice_correctlyWhenServiceIsCreated() {
        ServiceData service = ServiceDataMock.withDefaultValues();

        assertThat(service.getTotalPrice()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void shouldUpdateServiceData_withNewValues() {
        ServiceData service = ServiceDataMock.withDefaultValues();
        List<ServiceMaterial> newMaterials = new ArrayList<>();
        newMaterials.add(ServiceMaterialMock.withQuantity(3));

        service.update(
                "Novo serviço",
                "Nova descrição",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(150.00),
                newMaterials
        );

        assertThat(service.getName()).isEqualTo("Novo serviço");
        assertThat(service.getLaborCost()).isEqualTo(BigDecimal.valueOf(100.00));
    }

    @Test
    void shouldDeactivateService_settingDeletedAt() {
        ServiceData service = ServiceDataMock.withDefaultValues();

        service.deactivate();

        assertThat(service.getDeletedAt()).isNotNull();
        assertThat(service.isActive()).isFalse();
    }

    @Test
    void shouldReturnTrue_whenServiceIsActive() {
        ServiceData service = ServiceDataMock.withDefaultValues();

        assertThat(service.isActive()).isTrue();
    }

    @Test
    void shouldReturnFalse_whenServiceIsInactive() {
        ServiceData service = ServiceDataMock.inactive();

        assertThat(service.isActive()).isFalse();
    }
}






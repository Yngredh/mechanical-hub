package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.entities.mocks.ServiceMock;
import com.fiap.mechanical_hub.domain.entities.mocks.ServiceMaterialMock;
import com.fiap.mechanical_hub.domain.entities.constants.TestConstants;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    @Test
    void shouldCreateServiceWithValidData() {
        ServiceData service = ServiceMock.defaultService();

        assertNotNull(service.getId());
        assertEquals(TestConstants.DEFAULT_SERVICE_NAME, service.getName());
        assertEquals(TestConstants.DEFAULT_SERVICE_DESCRIPTION, service.getDescription());
        assertEquals(TestConstants.DEFAULT_SERVICE_LABOR_COST, service.getLaborCost());
        assertEquals(TestConstants.DEFAULT_SERVICE_BASE_PRICE, service.getBasePrice());
        assertTrue(service.isActive());
        assertNotNull(service.getTotalPrice());
    }

    @Test
    void shouldCalculateTotalPriceWithMaterials() {
        ServiceData service = ServiceMock.defaultService();

        BigDecimal materialsCost = TestConstants.DEFAULT_MATERIAL_UNIT_PRICE
                .multiply(BigDecimal.valueOf(TestConstants.DEFAULT_SERVICE_MATERIAL_QUANTITY));
        BigDecimal expectedTotalPrice = TestConstants.DEFAULT_SERVICE_LABOR_COST.add(materialsCost);

        assertEquals(expectedTotalPrice, service.getTotalPrice());
    }

    @Test
    void shouldCreateServiceWithEmptyMaterials() {
        ServiceData service = ServiceMock.serviceWithEmptyMaterials();

        assertNotNull(service.getId());
        assertEquals(TestConstants.DEFAULT_SERVICE_LABOR_COST, service.getTotalPrice());
    }

    @Test
    void shouldRecalculateTotalPriceWhenUpdating() {
        ServiceData service = ServiceMock.defaultService();
        BigDecimal originalPrice = service.getTotalPrice();

        List<ServiceMaterial> newMaterials = new ArrayList<>();
        newMaterials.add(ServiceMaterialMock.serviceMaterialWithCustomQuantity(10));
        
        service.update(
                "Serviço Atualizado",
                "Descrição atualizada",
                BigDecimal.valueOf(200.00),
                TestConstants.DEFAULT_SERVICE_BASE_PRICE,
                newMaterials
        );

        assertNotEquals(originalPrice, service.getTotalPrice());
    }

    @Test
    void shouldDeactivateService() {
        ServiceData service = ServiceMock.defaultService();
        assertTrue(service.isActive());

        service.deactivate();

        assertFalse(service.isActive());
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithBlankName() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.defaultServiceMaterial());

        assertThrows(BusinessRuleException.class, () ->
                ServiceData.create(
                    "",
                    TestConstants.DEFAULT_SERVICE_DESCRIPTION,
                    TestConstants.DEFAULT_SERVICE_LABOR_COST,
                    TestConstants.DEFAULT_SERVICE_BASE_PRICE,
                    materials
            )
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithNullName() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.defaultServiceMaterial());

        assertThrows(BusinessRuleException.class, () ->
                ServiceData.create(
                    null,
                    TestConstants.DEFAULT_SERVICE_DESCRIPTION,
                    TestConstants.DEFAULT_SERVICE_LABOR_COST,
                    TestConstants.DEFAULT_SERVICE_BASE_PRICE,
                    materials
            )
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithNegativeLaborCost() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.defaultServiceMaterial());

        BigDecimal negativePrice = BigDecimal.valueOf(-50.00);

        assertThrows(BusinessRuleException.class, () ->
                ServiceData.create(
                    TestConstants.DEFAULT_SERVICE_NAME,
                    TestConstants.DEFAULT_SERVICE_DESCRIPTION,
                    negativePrice,
                    TestConstants.DEFAULT_SERVICE_BASE_PRICE,
                    materials
            )
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithNegativeBasePrice() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.defaultServiceMaterial());

        BigDecimal negativePrice = BigDecimal.valueOf(-50.00);

        assertThrows(BusinessRuleException.class, () ->
                ServiceData.create(
                    TestConstants.DEFAULT_SERVICE_NAME,
                    TestConstants.DEFAULT_SERVICE_DESCRIPTION,
                    TestConstants.DEFAULT_SERVICE_LABOR_COST,
                    negativePrice,
                    materials
            )
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithNullMaterials() {
        assertThrows(BusinessRuleException.class, () ->
                ServiceData.create(
                    TestConstants.DEFAULT_SERVICE_NAME,
                    TestConstants.DEFAULT_SERVICE_DESCRIPTION,
                    TestConstants.DEFAULT_SERVICE_LABOR_COST,
                    TestConstants.DEFAULT_SERVICE_BASE_PRICE,
                    null
            )
        );
    }

    @Test
    void shouldCreateServiceWithMultipleMaterials() {
        ServiceData service = ServiceMock.serviceWithMultipleMaterials();

        assertNotNull(service.getId());
        assertTrue(service.getMaterials().size() > 1);
    }

    @Test
    void shouldKeepIdImmutable() {
        ServiceData service = ServiceMock.defaultService();
        java.util.UUID originalId = service.getId();

        service.deactivate();

        assertEquals(originalId, service.getId());
    }

}


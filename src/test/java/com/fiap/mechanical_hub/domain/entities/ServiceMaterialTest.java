package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.entities.mocks.ServiceMaterialMock;
import com.fiap.mechanical_hub.domain.entities.mocks.MaterialMock;
import com.fiap.mechanical_hub.domain.entities.constants.TestConstants;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ServiceMaterialTest {

    @Test
    void shouldCreateServiceMaterialWithValidData() {
        ServiceMaterial serviceMaterial = ServiceMaterialMock.defaultServiceMaterial();

        assertNotNull(serviceMaterial.getId());
        assertNotNull(serviceMaterial.getMaterial());
        assertEquals(TestConstants.DEFAULT_SERVICE_MATERIAL_QUANTITY, serviceMaterial.getQuantity());
    }

    @Test
    void shouldCalculateCostOfServiceMaterial() {
        ServiceMaterial serviceMaterial = ServiceMaterialMock.defaultServiceMaterial();

        BigDecimal expectedCost = TestConstants.DEFAULT_MATERIAL_UNIT_PRICE
                .multiply(BigDecimal.valueOf(TestConstants.DEFAULT_SERVICE_MATERIAL_QUANTITY));

        assertEquals(expectedCost, serviceMaterial.calculateCost());
    }

    @Test
    void shouldCreateServiceMaterialWithCustomQuantity() {
        int customQuantity = 10;
        ServiceMaterial serviceMaterial = ServiceMaterialMock.serviceMaterialWithCustomQuantity(customQuantity);

        assertEquals(customQuantity, serviceMaterial.getQuantity());
    }

    @Test
    void shouldCreateServiceMaterialWithCustomMaterial() {
        Material customMaterial = MaterialMock.materialWithHighPrice();
        ServiceMaterial serviceMaterial = ServiceMaterialMock.serviceMaterialWithCustomMaterial(customMaterial);

        assertEquals(customMaterial.getId(), serviceMaterial.getMaterial().getId());
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithNullMaterial() {
        assertThrows(BusinessRuleException.class, () ->
            ServiceMaterial.create(null, 5)
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithZeroQuantity() {
        Material material = MaterialMock.defaultMaterial();
        
        assertThrows(BusinessRuleException.class, () ->
            ServiceMaterial.create(material, 0)
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithNegativeQuantity() {
        Material material = MaterialMock.defaultMaterial();
        
        assertThrows(BusinessRuleException.class, () ->
            ServiceMaterial.create(material, -5)
        );
    }

    @Test
    void shouldCalculateCostCorrectlyWithDifferentQuantities() {
        Material material = MaterialMock.materialWithHighPrice();
        ServiceMaterial serviceMaterial = ServiceMaterialMock.serviceMaterialWithCustomValues(material, 3);

        BigDecimal expectedCost = material.getUnitPrice().multiply(BigDecimal.valueOf(3));

        assertEquals(expectedCost, serviceMaterial.calculateCost());
    }

    @Test
    void shouldMaintainMaterialReference() {
        Material material = MaterialMock.defaultMaterial();
        ServiceMaterial serviceMaterial = ServiceMaterialMock.serviceMaterialWithCustomMaterial(material);

        assertEquals(material.getId(), serviceMaterial.getMaterial().getId());
        assertEquals(material.getName(), serviceMaterial.getMaterial().getName());
    }

}


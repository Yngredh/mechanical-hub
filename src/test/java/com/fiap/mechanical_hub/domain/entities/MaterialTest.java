package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.entities.mocks.MaterialMock;
import com.fiap.mechanical_hub.domain.entities.constants.TestConstants;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MaterialTest {

    @Test
    void shouldCreateMaterialWithValidData() {
        Material material = MaterialMock.defaultMaterial();

        assertNotNull(material.getId());
        assertEquals(TestConstants.DEFAULT_MATERIAL_NAME, material.getName());
        assertEquals(TestConstants.DEFAULT_MATERIAL_DESCRIPTION, material.getDescription());
        assertEquals(TestConstants.DEFAULT_MATERIAL_UNIT_PRICE, material.getUnitPrice());
        assertEquals(TestConstants.DEFAULT_MATERIAL_MIN_STOCK, material.getMinStockQuantity());
        assertNotNull(material.getCreatedAt());
        assertNotNull(material.getUpdatedAt());
    }

    @Test
    void shouldUpdateMaterial() {
        Material material = MaterialMock.defaultMaterial();
        String newName = "Óleo Premium";
        String newDescription = "Óleo premium 10W40";
        BigDecimal newPrice = BigDecimal.valueOf(75.00);
        Integer newMinStock = 20;

        material.update(newName, newDescription, newPrice, newMinStock);

        assertEquals(newName, material.getName());
        assertEquals(newDescription, material.getDescription());
        assertEquals(newPrice, material.getUnitPrice());
        assertEquals(newMinStock, material.getMinStockQuantity());
    }

    @Test
    void shouldCreateMaterialWithHighPrice() {
        Material material = MaterialMock.materialWithHighPrice();

        assertNotNull(material.getId());
        assertTrue(material.getUnitPrice().compareTo(TestConstants.DEFAULT_MATERIAL_UNIT_PRICE) > 0);
    }

    @Test
    void shouldCreateMaterialWithLowMinStock() {
        Material material = MaterialMock.materialWithLowMinStock();

        assertEquals(1, material.getMinStockQuantity());
    }

    @Test
    void shouldKeepIdImmutable() {
        Material material = MaterialMock.defaultMaterial();
        java.util.UUID originalId = material.getId();

        material.update("New Name", "New Description", BigDecimal.TEN, 5);

        assertEquals(originalId, material.getId());
    }

    @Test
    void shouldKeepCreatedAtImmutable() {
        Material material = MaterialMock.defaultMaterial();
        java.time.LocalDateTime originalCreatedAt = material.getCreatedAt();

        material.update("New Name", "New Description", BigDecimal.TEN, 5);

        assertEquals(originalCreatedAt, material.getCreatedAt());
    }

}


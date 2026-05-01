package com.fiap.mechanical_hub.domain.entities.mocks;

import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.fiap.mechanical_hub.domain.entities.constants.TestConstants.*;

public class ServiceMock {

    public static ServiceData defaultService() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.defaultServiceMaterial());
        return ServiceData.create(
                DEFAULT_SERVICE_NAME,
                DEFAULT_SERVICE_DESCRIPTION,
                DEFAULT_SERVICE_LABOR_COST,
                DEFAULT_SERVICE_BASE_PRICE,
                materials
        );
    }

    public static ServiceData serviceWithEmptyMaterials() {
        return ServiceData.create(
                DEFAULT_SERVICE_NAME,
                DEFAULT_SERVICE_DESCRIPTION,
                DEFAULT_SERVICE_LABOR_COST,
                DEFAULT_SERVICE_BASE_PRICE,
                new ArrayList<>()
        );
    }

    public static ServiceData serviceWithMultipleMaterials() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.defaultServiceMaterial());
        materials.add(ServiceMaterialMock.serviceMaterialWithCustomQuantity(3));
        return ServiceData.create(
                DEFAULT_SERVICE_NAME,
                DEFAULT_SERVICE_DESCRIPTION,
                DEFAULT_SERVICE_LABOR_COST,
                DEFAULT_SERVICE_BASE_PRICE,
                materials
        );
    }

    public static ServiceData serviceWithHighPrice() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.defaultServiceMaterial());
        return ServiceData.create(
                "Serviço Premium",
                "Serviço de alta qualidade",
                BigDecimal.valueOf(500.00),
                BigDecimal.valueOf(1000.00),
                materials
        );
    }

}


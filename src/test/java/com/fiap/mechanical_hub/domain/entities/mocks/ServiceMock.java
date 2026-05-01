package com.fiap.mechanical_hub.domain.entities.mocks;

import com.fiap.mechanical_hub.domain.entities.Service;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.fiap.mechanical_hub.domain.entities.constants.TestConstants.*;

public class ServiceMock {

    public static Service defaultService() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.defaultServiceMaterial());
        return Service.create(
                DEFAULT_SERVICE_NAME,
                DEFAULT_SERVICE_DESCRIPTION,
                DEFAULT_SERVICE_LABOR_COST,
                DEFAULT_SERVICE_BASE_PRICE,
                materials
        );
    }

    public static Service serviceWithEmptyMaterials() {
        return Service.create(
                DEFAULT_SERVICE_NAME,
                DEFAULT_SERVICE_DESCRIPTION,
                DEFAULT_SERVICE_LABOR_COST,
                DEFAULT_SERVICE_BASE_PRICE,
                new ArrayList<>()
        );
    }

    public static Service serviceWithMultipleMaterials() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.defaultServiceMaterial());
        materials.add(ServiceMaterialMock.serviceMaterialWithCustomQuantity(3));
        return Service.create(
                DEFAULT_SERVICE_NAME,
                DEFAULT_SERVICE_DESCRIPTION,
                DEFAULT_SERVICE_LABOR_COST,
                DEFAULT_SERVICE_BASE_PRICE,
                materials
        );
    }

    public static Service serviceWithCustomValues(String name, String description, BigDecimal laborCost,
                                                   BigDecimal basePrice, List<ServiceMaterial> materials) {
        return Service.create(name, description, laborCost, basePrice, materials);
    }

    public static Service serviceWithHighPrice() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.defaultServiceMaterial());
        return Service.create(
                "Serviço Premium",
                "Serviço de alta qualidade",
                BigDecimal.valueOf(500.00),
                BigDecimal.valueOf(1000.00),
                materials
        );
    }

}


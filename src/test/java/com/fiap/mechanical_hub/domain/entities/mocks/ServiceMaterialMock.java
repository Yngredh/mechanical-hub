package com.fiap.mechanical_hub.domain.entities.mocks;

import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.domain.entities.Material;

import static com.fiap.mechanical_hub.domain.entities.constants.TestConstants.*;

public class ServiceMaterialMock {

    public static ServiceMaterial defaultServiceMaterial() {
        Material material = MaterialMock.defaultMaterial();
        return ServiceMaterial.create(
                material,
                DEFAULT_SERVICE_MATERIAL_QUANTITY
        );
    }

    public static ServiceMaterial serviceMaterialWithCustomQuantity(int quantity) {
        Material material = MaterialMock.defaultMaterial();
        return ServiceMaterial.create(
                material,
                quantity
        );
    }

    public static ServiceMaterial serviceMaterialWithCustomMaterial(Material material) {
        return ServiceMaterial.create(
                material,
                DEFAULT_SERVICE_MATERIAL_QUANTITY
        );
    }

    public static ServiceMaterial serviceMaterialWithCustomValues(Material material, int quantity) {
        return ServiceMaterial.create(material, quantity);
    }

}


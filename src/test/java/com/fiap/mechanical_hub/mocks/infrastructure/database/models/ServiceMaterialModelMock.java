package com.fiap.mechanical_hub.mocks.infrastructure.database.models;

import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;

import java.util.UUID;

public class ServiceMaterialModelMock {

    public static final UUID SERVICE_MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000050");

    public static ServiceMaterialModel withDefaultValues() {
        ServiceModel serviceRef = new ServiceModel();
        serviceRef.setId(ServiceModelMock.SERVICE_ID);
        serviceRef.setUpdatedAt(ServiceModelMock.UPDATED_AT);

        ServiceMaterialModel model = new ServiceMaterialModel();
        model.setId(SERVICE_MATERIAL_ID);
        model.setService(serviceRef);
        model.setMaterial(MaterialModelMock.withDefaultValues());
        model.setQuantity(2);
        return model;
    }
}

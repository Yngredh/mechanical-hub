package com.fiap.mechanical_hub.mocks.infrastructure.database.models;

import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ServiceModelMock {

    public static final UUID SERVICE_ID = UUID.fromString("00000000-0000-0000-0000-000000000040");
    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);
    public static final LocalDateTime UPDATED_AT = LocalDateTime.of(2024, 1, 2, 10, 0);

    public static ServiceModel withNoMaterials() {
        ServiceModel model = new ServiceModel();
        model.setId(SERVICE_ID);
        model.setName("Troca de óleo");
        model.setDescription("Troca de óleo e filtro");
        model.setLaborCost(BigDecimal.valueOf(50.00));
        model.setBasePrice(BigDecimal.valueOf(80.00));
        model.setTotalPrice(BigDecimal.valueOf(130.00));
        model.setActive(true);
        model.setCreatedAt(CREATED_AT);
        model.setUpdatedAt(UPDATED_AT);
        model.setDeletedAt(null);
        return model;
    }

    public static ServiceModel withOneMaterial() {
        ServiceModel model = withNoMaterials();

        ServiceMaterialModel sm = new ServiceMaterialModel();
        sm.setId(UUID.fromString("00000000-0000-0000-0000-000000000050"));
        sm.setService(model);
        sm.setMaterial(MaterialModelMock.withDefaultValues());
        sm.setQuantity(2);

        model.getMaterials().add(sm);
        return model;
    }
}

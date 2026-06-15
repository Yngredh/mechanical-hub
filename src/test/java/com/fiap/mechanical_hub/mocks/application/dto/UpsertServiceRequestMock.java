package com.fiap.mechanical_hub.mocks.application.dto;

import com.fiap.mechanical_hub.application.dto.service.UpsertServiceRequest;
import com.fiap.mechanical_hub.application.dto.servicematerials.ServiceMaterialRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class UpsertServiceRequestMock {

    public static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    public static UpsertServiceRequest withDefaultValues() {
        ServiceMaterialRequest materialRequest = new ServiceMaterialRequest(MATERIAL_ID, 2);

        return new UpsertServiceRequest(
                "Troca de óleo",
                "Troca de óleo e filtro",
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(80.00),
                List.of(materialRequest)
        );
    }
}

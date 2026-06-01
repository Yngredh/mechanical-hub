package com.fiap.mechanical_hub.mocks.domain.entities;

import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceDataMock {

    private static final UUID SERVICE_ID = UUID.fromString("00000000-0000-0000-0000-000000000040");

    public static ServiceData withDefaultValues() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.withDefaultValues());

        return new ServiceData(
                SERVICE_ID,
                "Troca de óleo",
                "Troca de óleo e filtro",
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(80.00),
                BigDecimal.valueOf(130.00),
                materials,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static ServiceData inactive() {
        List<ServiceMaterial> materials = new ArrayList<>();
        materials.add(ServiceMaterialMock.withDefaultValues());

        return new ServiceData(
                SERVICE_ID,
                "Serviço inativo",
                "Serviço inativo",
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(80.00),
                BigDecimal.valueOf(130.00),
                materials,
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}


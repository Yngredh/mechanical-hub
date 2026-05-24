package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import java.util.List;
import java.util.UUID;

public interface ServiceMaterialRepository {

    ServiceMaterial save(ServiceMaterial serviceMaterial, ServiceData serviceData);

    List<ServiceMaterial> findByServiceId(UUID serviceId);

    List<ServiceMaterial> findByMaterialId(UUID materialId);

}


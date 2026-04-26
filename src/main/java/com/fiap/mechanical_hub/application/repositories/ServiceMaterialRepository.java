package com.fiap.mechanical_hub.application.repositories;

import com.fiap.mechanical_hub.domain.entities.Service;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import java.util.List;
import java.util.UUID;

public interface ServiceMaterialRepository {

    ServiceMaterial save(ServiceMaterial serviceMaterial, Service service);

    List<ServiceMaterial> findByServiceId(UUID serviceId);

    void deleteById(UUID id);
}


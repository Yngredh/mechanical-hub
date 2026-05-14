package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.domain.repositories.ServiceMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceMaterialUseCase {

    private final ServiceMaterialRepository serviceMaterialRepository;

    public List<ServiceMaterial> getServiceMaterials(UUID serviceId) {
        return serviceMaterialRepository.findByServiceId(serviceId);
    }
}

package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.application.dto.service.UpsertServiceRequest;
import com.fiap.mechanical_hub.application.mappers.ServiceMapper;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ServiceUseCase {

    private final MaterialRepository materialRepository;
    private final ServiceRepository serviceRepository;

    public ServiceResponse create(UpsertServiceRequest request) {
        log.info("Creating new service with name: {}", request.getName());
        List<ServiceMaterial> materials = request.getMaterials().stream()
                .map(m -> {
                    Material material = materialRepository.findById(m.getMaterialId())
                            .orElseThrow(() -> new NotFoundException("Material not found: " + m.getMaterialId()));
                    return ServiceMaterial.create(material, m.getQuantity());
                })
                .toList();

        ServiceData serviceData = ServiceData.create(
                request.getName(),
                request.getDescription(),
                request.getLaborCost(),
                request.getBasePrice(),
                materials
        );

        serviceRepository.save(serviceData);

        return ServiceMapper.toResponse(serviceData);
    }

    public ServiceResponse update(UUID id, UpsertServiceRequest request) {
        ServiceData serviceData = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        List<ServiceMaterial> materials = request.getMaterials().stream()
                .map(m -> {
                    Material material = materialRepository.findById(m.getMaterialId())
                            .orElseThrow(() -> new NotFoundException("Material not found: " + m.getMaterialId()));
                    return ServiceMaterial.create(material, m.getQuantity());
                })
                .toList();

        serviceData.update(
                request.getName(),
                request.getDescription(),
                request.getLaborCost(),
                request.getBasePrice(),
                materials
        );

        serviceRepository.save(serviceData);

        return ServiceMapper.toResponse(serviceData);
    }

    @Transactional(readOnly = true)
    public ServiceResponse findById(UUID id) {
        Optional<ServiceData> service = serviceRepository.findById(id);
        if (service.isEmpty()) { throw new NotFoundException("Service with id " + id + " not found"); }
        return ServiceMapper.toResponse(service.get());
    }

    public ServiceData findServiceById(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service not found"));
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> findAll() {
        return serviceRepository.findAll().stream()
                .map(ServiceMapper::toResponse)
                .toList();
    }

    public void delete(UUID id) {
        serviceRepository.deleteById(id);
    }

    public List<ServiceData> findAll(List<UUID> serviceIds) {
        return serviceRepository.findByIds(serviceIds);
    }
}
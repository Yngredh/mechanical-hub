package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.application.dto.service.UpsertServiceRequest;
import com.fiap.mechanical_hub.application.mappers.ServiceMapper;
import com.fiap.mechanical_hub.application.repositories.ServiceRepository;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.Service;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class ServiceUseCase {

    private final MaterialUseCase materialUseCase;
    private final ServiceRepository serviceRepository;

    public ServiceResponse create(UpsertServiceRequest request) {
        List<ServiceMaterial> materials = request.getMaterials().stream()
                .map(m -> {
                    Material material = materialUseCase.findById(m.getMaterialId());
                    return new ServiceMaterial(material, m.getQuantity());
                })
                .toList();

        Service service = Service.create(
                request.getName(),
                request.getDescription(),
                request.getLaborCost(),
                request.getBasePrice(),
                materials
        );

        serviceRepository.save(service);

        return ServiceMapper.toResponse(service);
    }

    public ServiceResponse update(UUID id, UpsertServiceRequest request) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        List<ServiceMaterial> materials = request.getMaterials().stream()
                .map(m -> {
                    Material material = materialUseCase.findById(m.getMaterialId());

                    return new ServiceMaterial(material, m.getQuantity());
                })
                .toList();

        service.update(
                request.getName(),
                request.getDescription(),
                request.getLaborCost(),
                request.getBasePrice(),
                materials
        );

        serviceRepository.save(service);

        return ServiceMapper.toResponse(service);
    }

    @Transactional(readOnly = true)
    public ServiceResponse findById(UUID id) {
        Optional<Service> service = serviceRepository.findById(id);
        if (service.isEmpty()) { throw new NotFoundException("Service with id " + id + " not found"); }
        return ServiceMapper.toResponse(service.get());
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
}
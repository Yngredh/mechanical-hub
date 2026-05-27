package com.fiap.mechanical_hub.application.usecases.service;

import com.fiap.mechanical_hub.application.command.ordertask.UpdateServiceCommand;
import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.infrastructure.http.mappers.ServiceHttpMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateServiceUseCase {

    private final MaterialRepository materialRepository;
    private final ServiceRepository serviceRepository;

    @Transactional
    public ServiceResponse execute(UpdateServiceCommand command) {
        log.info("Updating service with id: {}", command.id());

        ServiceData serviceData = serviceRepository.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));

        List<ServiceMaterial> materials = command.materials().stream()
                .map(m -> {
                    Material material = materialRepository.findById(m.getMaterialId())
                            .orElseThrow(() -> new NotFoundException("Material não encontrado: " + m.getMaterialId()));
                    return ServiceMaterial.create(material, m.getQuantity());
                })
                .toList();

        serviceData.update(
                command.name(),
                command.description(),
                command.laborCost(),
                command.basePrice(),
                materials
        );

        ServiceData updatedService = serviceRepository.save(serviceData);
        log.info("Service updated with id: {}", updatedService.getId());

        return ServiceHttpMapper.toResponse(updatedService);
    }
}


package com.fiap.mechanical_hub.application.usecases.service;

import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetServiceByIdUseCase {

    private final ServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public ServiceData execute(UUID serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
    }
}


package com.fiap.mechanical_hub.application.usecases.service;

import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
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
public class FindAllServicesUseCase {

    private final ServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public List<ServiceResponse> execute() {
        log.info("Finding all services");

        List<ServiceResponse> services = serviceRepository.findAll().stream()
                .map(ServiceHttpMapper::toResponse)
                .toList();

        log.info("Found {} services", services.size());
        return services;
    }
}


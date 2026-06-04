package com.fiap.mechanical_hub.application.usecases.service;

import com.fiap.mechanical_hub.application.command.ordertask.FindOrderTaskByIdCommand;
import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.application.mappers.ServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindServiceByIdUseCase {

    private final ServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public ServiceResponse execute(FindOrderTaskByIdCommand command) {
        log.info("Finding service by id: {}", command.id());

        ServiceData serviceData = serviceRepository.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Serviço com id " + command.id() + " não encontrado"));

        log.info("Service found with id: {}", command.id());
        return ServiceMapper.toResponse(serviceData);
    }
}


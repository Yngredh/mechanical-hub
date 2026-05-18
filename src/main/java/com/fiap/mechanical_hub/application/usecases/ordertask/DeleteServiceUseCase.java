package com.fiap.mechanical_hub.application.usecases.ordertask;

import com.fiap.mechanical_hub.application.command.ordertask.DeleteOrderTaskCommand;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteServiceUseCase {

    private final ServiceRepository serviceRepository;

    @Transactional
    public void execute(DeleteOrderTaskCommand command) {
        log.info("Deleting service with id: {}", command.id());

        if (!serviceRepository.existsById(command.id())) {
            throw new NotFoundException("Serviço não encontrado");
        }

        serviceRepository.deleteById(command.id());
        log.info("Service deleted with id: {}", command.id());
    }
}


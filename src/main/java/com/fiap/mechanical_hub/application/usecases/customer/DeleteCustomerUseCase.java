package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteCustomerUseCase {

    private final CustomerRepository repository;

    @Transactional
    public void execute(UUID id) {
        log.info("Deleting customer with id: {}", id);

        if (repository.findById(id).isEmpty()) throw new NotFoundException("Cliente não encontrado para o id: " + id);

        // TODO Ao excluir um cliente, seu veículo e ordens devem ser excluídos juntos
        repository.deleteById(id);
        log.info("Customer with id: {} deleted successfully", id);
    }
}


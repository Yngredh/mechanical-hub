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
    public void execute(UUID customerId) {
        log.info("Deleting customer with id: {}", customerId);

        if (repository.findById(customerId).isEmpty()) {
            throw new NotFoundException("Cliente não encontrado para o id: " + customerId);
        }



        repository.deleteById(customerId);
        log.info("Customer with id: {} deleted successfully", customerId);
    }
}


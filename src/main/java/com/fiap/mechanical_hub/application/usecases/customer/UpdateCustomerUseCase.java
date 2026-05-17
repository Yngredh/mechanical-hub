package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.command.customer.UpdateCustomerCommand;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fiap.mechanical_hub.infrastructure.http.mappers.CustomerHttpMapper.toResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateCustomerUseCase {

    private final CustomerRepository repository;

    @Transactional
    public CustomerResponse execute(UpdateCustomerCommand command) {
        log.info("Updating customer with id: {}", command.id());

        Customer existingCustomer = repository.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado para o id: " + command.id()));

        existingCustomer.update(
                command.name(),
                command.telephone(),
                command.email(),
                command.address()
        );

        Customer updatedCustomer = repository.save(existingCustomer);
        log.info("Customer with id: {} updated successfully", command.id());

        return toResponse(updatedCustomer);
    }
}


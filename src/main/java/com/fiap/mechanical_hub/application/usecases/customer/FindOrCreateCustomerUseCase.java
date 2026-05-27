package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.application.command.customer.FindOrCreateCustomerCommand;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindOrCreateCustomerUseCase {

    private final CustomerRepository repository;

    @Transactional
    public Customer execute(FindOrCreateCustomerCommand command) {
        log.info("Finding or creating customer with document: {}", command.documentNumber());

        Optional<Customer> existingCustomer = repository.findByDocumentNumber(command.documentNumber());
        if (existingCustomer.isPresent()) {
            log.info("Customer found with document: {}", command.documentNumber());
            return existingCustomer.get();
        }

        log.info("Creating new customer with document: {}", command.documentNumber());

        Document document = new Document(
                DocumentTypeEnum.fromValue(command.documentType()), command.documentNumber());

        Customer newCustomer = Customer.create(
                command.name(),
                document,
                command.telephone(),
                command.email(),
                command.address()
        );
        return repository.save(newCustomer);
    }
}


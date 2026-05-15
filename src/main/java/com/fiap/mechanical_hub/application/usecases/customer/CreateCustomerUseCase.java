package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.command.customer.CreateCustomerCommand;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.service.CustomerDomainService;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateCustomerUseCase {

    private final CustomerRepository repository;
    private final CustomerDomainService domainService;

    @Transactional
    public CustomerResponse execute(CreateCustomerCommand command) {

        Document document = new Document(
                DocumentTypeEnum.fromValue(command.documentType()), command.documentNumber());

        domainService.validateUniqueDocument(document);

        Customer customer = Customer.create(
                command.name(),
                document,
                command.telephone(),
                command.email(),
                command.address()
        );

        repository.save(customer);

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getDocument().getNumber(),
                customer.getEmail()
        );
    }

}

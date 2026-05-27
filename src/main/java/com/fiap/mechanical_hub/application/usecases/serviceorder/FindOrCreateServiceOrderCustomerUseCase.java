package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.customer.FindOrCreateCustomerCommand;
import com.fiap.mechanical_hub.application.usecases.customer.FindOrCreateCustomerUseCase;
import com.fiap.mechanical_hub.domain.entities.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindOrCreateServiceOrderCustomerUseCase {

    private final FindOrCreateCustomerUseCase findOrCreateCustomerUseCase;

    @Transactional
    public Customer execute(
        String name,
        String documentType,
        String documentNumber,
        String telephone,
        String email,
        String address
    ) {
        var command = new FindOrCreateCustomerCommand(name, documentType, documentNumber, telephone, email, address);
        return findOrCreateCustomerUseCase.execute(command);
    }
}




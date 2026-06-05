package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.customer.InsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.command.customer.CreateCustomerCommand;
import com.fiap.mechanical_hub.application.command.customer.UpdateCustomerCommand;
import com.fiap.mechanical_hub.application.dto.customer.UpdateCustomerRequest;
import com.fiap.mechanical_hub.domain.entities.Customer;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.fiap.mechanical_hub.domain.utils.document.DocumentFormatter.formatDocument;
import static com.fiap.mechanical_hub.domain.utils.telephone.TelephoneFormatter.formatTelephone;

@Component
public class CustomerMapper {

    public static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                formatDocument(
                        customer.getDocument().getType().getValue(),
                        customer.getDocument().getNumber()),
                customer.getEmail(),
                formatTelephone(customer.getTelephone())
        );
    }

    public CreateCustomerCommand toCommand(InsertCustomerRequest request) {
        return new CreateCustomerCommand(
                request.getName(),
                request.getDocumentType(),
                request.getDocumentNumber(),
                request.getTelephone(),
                request.getEmail(),
                request.getAddress()
        );
    }

    public UpdateCustomerCommand toUpdateCommand(UUID id, UpdateCustomerRequest request) {
        return new UpdateCustomerCommand(
                id,
                request.getName(),
                request.getTelephone(),
                request.getEmail(),
                request.getAddress()
        );
    }

}

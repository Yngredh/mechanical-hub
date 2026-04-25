package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.customer.UpsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import org.springframework.stereotype.Component;

import static com.fiap.mechanical_hub.shared.utils.document.DocumentFormatter.formatDocument;
import static com.fiap.mechanical_hub.shared.utils.telephone.TelephoneFormatter.formatTelephone;

@Component
public class CustomerMapper {

    public Customer toDomainEntity(UpsertCustomerRequest request) {
        DocumentTypeEnum documentTypeEnum = DocumentTypeEnum.fromValue(request.getDocumentType());
        return Customer.create(
                request.getName(),
                documentTypeEnum,
                request.getDocumentNumber(),
                request.getTelephone(),
                request.getEmail(),
                request.getAddress()
        );
    }

    public Customer toDomainEntity(UpsertCustomerRequest request, Customer existingCustomer) {
        DocumentTypeEnum documentTypeEnum = DocumentTypeEnum.fromValue(request.getDocumentType());
        existingCustomer.update(
                request.getName(),
                documentTypeEnum,
                request.getDocumentNumber(),
                request.getTelephone(),
                request.getEmail(),
                request.getAddress()
        );
        return existingCustomer;
    }

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getDocumentTypeEnum().getValue(),
                formatDocument(
                        customer.getDocumentTypeEnum().getValue(),
                        customer.getDocumentNumber()
                ),
                formatTelephone(customer.getTelephone()),
                customer.getEmail(),
                customer.getAddress(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}


package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.customer.UpsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentType;
import org.springframework.stereotype.Component;

import static com.fiap.mechanical_hub.shared.utils.document.DocumentFormatter.formatDocument;
import static com.fiap.mechanical_hub.shared.utils.telephone.TelephoneFormatter.formatTelephone;
import static com.fiap.mechanical_hub.shared.utils.telephone.TelephoneFormatter.formatTelephone;

@Component
public class CustomerMapper {

    public Customer toDomainEntity(UpsertCustomerRequest request) {
        DocumentType documentType = DocumentType.fromValue(request.getDocumentType());
        return Customer.create(
                request.getName(),
                documentType,
                request.getDocumentNumber(),
                request.getTelephone(),
                request.getEmail(),
                request.getAddress()
        );
    }

    public Customer toDomainEntity(UpsertCustomerRequest request, Customer existingCustomer) {
        DocumentType documentType = DocumentType.fromValue(request.getDocumentType());
        existingCustomer.updateCustomerInfo(
                request.getName(),
                documentType,
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
                customer.getDocumentType().getValue(),
                formatDocument(
                        customer.getDocumentType().getValue(),
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


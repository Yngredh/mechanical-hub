package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.DocumentType;
import com.fiap.mechanical_hub.domain.exceptions.InvalidDocumentException;
import com.fiap.mechanical_hub.shared.utils.DocumentValidator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private UUID id;
    private String name;
    private DocumentType documentType;
    private String documentNumber;
    private String telephone;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Customer create(String name, DocumentType documentType, String documentNumber,
                                   String telephone, String email, String address) {
        validateDocument(documentType, documentNumber);

        Customer customer = new Customer();
        customer.id = UUID.randomUUID();
        customer.name = name;
        customer.documentType = documentType;
        customer.documentNumber = documentNumber;
        customer.telephone = telephone;
        customer.email = email;
        customer.address = address;
        customer.createdAt = LocalDateTime.now();
        customer.updatedAt = LocalDateTime.now();

        return customer;
    }

    public void updateCustomerInfo(String name, DocumentType documentType, String documentNumber,
                                    String telephone, String email, String address) {
        validateDocument(documentType, documentNumber);

        this.name = name;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.telephone = telephone;
        this.email = email;
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    private static void validateDocument(DocumentType documentType, String documentNumber) {
        boolean isValid = documentType == DocumentType.CPF
                ? DocumentValidator.isValidCPF(documentNumber)
                : DocumentValidator.isValidCNPJ(documentNumber);

        if (!isValid) {
            throw new InvalidDocumentException(
                    String.format("Invalid %s: %s", documentType.getValue(), documentNumber)
            );
        }
    }
}


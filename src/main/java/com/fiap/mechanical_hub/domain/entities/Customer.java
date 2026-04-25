package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.fiap.mechanical_hub.shared.utils.Formatter.removeFormatting;
import static com.fiap.mechanical_hub.shared.utils.document.DocumentValidator.validateDocument;
import static com.fiap.mechanical_hub.shared.utils.telephone.TelephoneValidator.validateTelephone;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private UUID id;
    private String name;
    private DocumentTypeEnum documentTypeEnum;
    private String documentNumber;
    private String telephone;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Customer create(String name, DocumentTypeEnum documentTypeEnum, String documentNumber,
                                  String telephone, String email, String address) {
        validateDocument(documentTypeEnum, documentNumber);
        validateTelephone(telephone);

        Customer customer = new Customer();
        customer.id = UUID.randomUUID();
        customer.name = name;
        customer.documentTypeEnum = documentTypeEnum;
        customer.documentNumber = removeFormatting(documentNumber);
        customer.telephone = removeFormatting(telephone);
        customer.email = email;
        customer.address = address;
        customer.createdAt = LocalDateTime.now();
        customer.updatedAt = LocalDateTime.now();

        return customer;
    }

    public void update(String name, DocumentTypeEnum documentTypeEnum, String documentNumber,
                       String telephone, String email, String address) {
        validateDocument(documentTypeEnum, documentNumber);
        validateTelephone(telephone);

        this.name = name;
        this.documentTypeEnum = documentTypeEnum;
        this.documentNumber = removeFormatting(documentNumber);
        this.telephone = removeFormatting(telephone);
        this.email = email;
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

}


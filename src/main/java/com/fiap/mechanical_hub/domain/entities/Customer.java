package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.fiap.mechanical_hub.domain.utils.Formatter.removeFormatting;
import static com.fiap.mechanical_hub.domain.utils.document.DocumentValidator.validateDocument;
import static com.fiap.mechanical_hub.domain.utils.telephone.TelephoneValidator.validateTelephone;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private UUID id;
    private String name;
    private Document document;
    private String telephone;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Customer create(String name, Document document, String telephone, String email, String address) {
        validateTelephone(telephone);

        Customer customer = new Customer();
        customer.id = UUID.randomUUID();
        customer.name = name;
        customer.document = document;
        customer.telephone = removeFormatting(telephone);
        customer.email = email;
        customer.address = address;
        customer.createdAt = LocalDateTime.now();
        customer.updatedAt = LocalDateTime.now();

        return customer;
    }

    public void update(String name, String telephone, String email, String address) {
        validateTelephone(telephone);

        this.name = name;
        this.telephone = removeFormatting(telephone);
        this.email = email;
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

}


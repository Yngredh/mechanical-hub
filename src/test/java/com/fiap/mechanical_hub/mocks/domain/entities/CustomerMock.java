package com.fiap.mechanical_hub.mocks.domain.entities;

import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;

public class CustomerMock {

    public static Customer withDefaultValues() {
        Document document = new Document(DocumentTypeEnum.CPF, "12345678901");
        return Customer.create(
                "João Silva",
                document,
                "11987654321",
                "joao@email.com",
                "Rua A, 123"
        );
    }

    public static Customer withName(String name) {
        Document document = new Document(DocumentTypeEnum.CPF, "12345678901");
        return Customer.create(
                name,
                document,
                "11987654321",
                "joao@email.com",
                "Rua A, 123"
        );
    }

    public static Customer inactive() {
        Customer customer = withDefaultValues();
        customer.deactivate();
        return customer;
    }
}



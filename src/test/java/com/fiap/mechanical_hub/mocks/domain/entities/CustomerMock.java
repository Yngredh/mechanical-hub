package com.fiap.mechanical_hub.mocks.domain.entities;

import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;

public class CustomerMock {

    public static final String VALID_CPF = "52998224725";
    public static final String VALID_TELEPHONE = "5511987654321";

    public static Customer withDefaultValues() {
        Document document = new Document(DocumentTypeEnum.CPF, VALID_CPF);
        return Customer.create(
                "João Silva",
                document,
                VALID_TELEPHONE,
                "joao@email.com",
                "Rua A, 123"
        );
    }

    public static Customer withName(String name) {
        Document document = new Document(DocumentTypeEnum.CPF, VALID_CPF);
        return Customer.create(
                name,
                document,
                VALID_TELEPHONE,
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

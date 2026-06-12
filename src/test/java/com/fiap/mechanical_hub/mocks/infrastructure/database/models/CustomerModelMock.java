package com.fiap.mechanical_hub.mocks.infrastructure.database.models;

import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.CustomerModel;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerModelMock {

    public static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000060");
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2024, 1, 2, 10, 0);

    public static CustomerModel withDefaultValues() {
        return new CustomerModel(
                CUSTOMER_ID,
                "João Silva",
                DocumentTypeEnum.CPF,
                "52998224725",
                "5511987654321",
                "joao@email.com",
                "Rua A, 123",
                CREATED_AT,
                UPDATED_AT,
                null
        );
    }
}

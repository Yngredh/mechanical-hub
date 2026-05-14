package com.fiap.mechanical_hub.infrastructure.mappers;

import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import com.fiap.mechanical_hub.infrastructure.database.models.CustomerModel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerRepositoryMapper {

    public static CustomerModel toJpaEntity(Customer customer) {
        return new CustomerModel(
                customer.getId(),
                customer.getName(),
                customer.getDocument().getType(),
                customer.getDocument().getNumber(),
                customer.getTelephone(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    public static Customer toDomainEntity(CustomerModel entity) {
        Document document = new Document(
                entity.getDocumentTypeEnum(), entity.getDocumentNumber());

        return new Customer(
                entity.getId(),
                entity.getName(),
                document,
                entity.getTelephone(),
                entity.getEmail(),
                entity.getAddress(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

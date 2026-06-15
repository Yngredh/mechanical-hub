package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.CustomerModel;
import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.CustomerModelMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRepositoryMapperTest {

    @Test
    void shouldMapAllFields_whenConvertingDomainToJpaEntity() {
        Customer customer = CustomerMock.withDefaultValues();

        CustomerModel model = CustomerRepositoryMapper.toJpaEntity(customer);

        assertThat(model.getId()).isEqualTo(customer.getId());
        assertThat(model.getName()).isEqualTo(customer.getName());
        assertThat(model.getDocumentTypeEnum()).isEqualTo(customer.getDocument().getType());
        assertThat(model.getDocumentNumber()).isEqualTo(customer.getDocument().getNumber());
        assertThat(model.getTelephone()).isEqualTo(customer.getTelephone());
        assertThat(model.getEmail()).isEqualTo(customer.getEmail());
        assertThat(model.getAddress()).isEqualTo(customer.getAddress());
    }

    @Test
    void shouldMapAllFields_whenConvertingJpaEntityToDomainEntity() {
        CustomerModel model = CustomerModelMock.withDefaultValues();

        Customer customer = CustomerRepositoryMapper.toDomainEntity(model);

        assertThat(customer.getId()).isEqualTo(model.getId());
        assertThat(customer.getName()).isEqualTo(model.getName());
        assertThat(customer.getDocument().getType()).isEqualTo(model.getDocumentTypeEnum());
        assertThat(customer.getDocument().getNumber()).isEqualTo(model.getDocumentNumber());
        assertThat(customer.getTelephone()).isEqualTo(model.getTelephone());
        assertThat(customer.getEmail()).isEqualTo(model.getEmail());
        assertThat(customer.getAddress()).isEqualTo(model.getAddress());
    }

    @Test
    void shouldReconstructDocument_whenMappingFromJpaEntity() {
        CustomerModel model = CustomerModelMock.withDefaultValues();

        Customer customer = CustomerRepositoryMapper.toDomainEntity(model);

        assertThat(customer.getDocument()).isNotNull();
        assertThat(customer.getDocument().getType()).isEqualTo(DocumentTypeEnum.CPF);
    }
}

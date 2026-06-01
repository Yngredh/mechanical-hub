package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void shouldCreateCustomer_withValidData() {
        Document document = new Document(DocumentTypeEnum.CPF, "12345678901");

        Customer customer = Customer.create(
                "João Silva",
                document,
                "11987654321",
                "joao@email.com",
                "Rua A, 123"
        );

        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getName()).isEqualTo("João Silva");
        assertThat(customer.getDocument()).isEqualTo(document);
        assertThat(customer.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateCustomer_withNewData() {
        Customer customer = CustomerMock.withDefaultValues();

        customer.update(
                "Maria Silva",
                "11987654321",
                "maria@email.com",
                "Rua B, 456"
        );

        assertThat(customer.getName()).isEqualTo("Maria Silva");
        assertThat(customer.getEmail()).isEqualTo("maria@email.com");
        assertThat(customer.getAddress()).isEqualTo("Rua B, 456");
    }

    @Test
    void shouldDeactivateCustomer_settingDeletedAt() {
        Customer customer = CustomerMock.withDefaultValues();

        customer.deactivate();

        assertThat(customer.getDeletedAt()).isNotNull();
        assertThat(customer.isActive()).isFalse();
    }

    @Test
    void shouldReturnTrue_whenCustomerIsActive() {
        Customer customer = CustomerMock.withDefaultValues();

        assertThat(customer.isActive()).isTrue();
    }

    @Test
    void shouldReturnFalse_whenCustomerIsInactive() {
        Customer customer = CustomerMock.inactive();

        assertThat(customer.isActive()).isFalse();
    }

    @Test
    void shouldRemoveFormattingFromTelephone_whenCreatingCustomer() {
        Document document = new Document(DocumentTypeEnum.CPF, "12345678901");

        Customer customer = Customer.create(
                "João Silva",
                document,
                "(11) 98765-4321",
                "joao@email.com",
                "Rua A, 123"
        );

        assertThat(customer.getTelephone()).isNotNull();
    }

    @Test
    void shouldMaintainIdAfterUpdate() {
        Customer customer = CustomerMock.withDefaultValues();
        var originalId = customer.getId();

        customer.update(
                "Novo nome",
                "11987654321",
                "novo@email.com",
                "Nova rua"
        );

        assertThat(customer.getId()).isEqualTo(originalId);
    }
}


package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.DocumentType;
import com.fiap.mechanical_hub.domain.exceptions.InvalidDocumentException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidTelephoneException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da Entidade Customer")
class CustomerTest {

    @Nested
    @DisplayName("Validação de CPF")
    class CPFValidationTests {

        @Test
        @DisplayName("Deve criar customer com CPF válido formatado")
        void shouldCreateCustomerWithValidFormattedCPF() {
            String validCPF = "111.444.777-35";

            Customer customer = Customer.create(
                    "João Silva",
                    DocumentType.CPF,
                    validCPF,
                    "+55 (11) 98765-4321",
                    "joao@example.com",
                    "Rua A, 123"
            );

            assertThat(customer).isNotNull();
            assertThat(customer.getDocumentNumber()).isEqualTo("11144477735");
            assertThat(customer.getDocumentType()).isEqualTo(DocumentType.CPF);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "111.111.111-11",
                "000.000.000-00",
                "123.456.789-00",
                "12.345.678-90",
                "abcdefghijk",
                "123",
                ""
        })
        @DisplayName("Não deve criar customer com CPF inválido")
        void shouldThrowExceptionWithInvalidCPF(String invalidCPF) {
            assertThatThrownBy(() -> Customer.create(
                    "João Silva",
                    DocumentType.CPF,
                    invalidCPF,
                    "+55 (11) 98765-4321",
                    "joao@example.com",
                    "Rua A, 123"
            ))
                    .isInstanceOf(InvalidDocumentException.class)
                    .hasMessageContaining("CPF");
        }

        @Test
        @DisplayName("Não deve criar customer com CPF nulo")
        void shouldThrowExceptionWithNullCPF() {
            assertThatThrownBy(() -> Customer.create(
                    "João Silva",
                    DocumentType.CPF,
                    null,
                    "+55 (11) 98765-4321",
                    "joao@example.com",
                    "Rua A, 123"
            ))
                    .isInstanceOf(InvalidDocumentException.class);
        }

        @Test
        @DisplayName("Deve reconhecer CPF válido: 11144477735")
        void shouldValidateCorrectCPF() {
            assertThatCode(() -> Customer.create(
                    "Cliente",
                    DocumentType.CPF,
                    "111.444.777-35",
                    "+55 (11) 98765-4321",
                    "cliente@example.com",
                    "Rua"
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Deve rejeitar CPF inválido: 123.456.789-10")
        void shouldRejectInvalidCPFCheckDigits() {
            assertThatThrownBy(() -> Customer.create(
                    "Cliente",
                    DocumentType.CPF,
                    "123.456.789-10",
                    "+55 (11) 98765-4321",
                    "cliente@example.com",
                    "Rua"
            ))
                    .isInstanceOf(InvalidDocumentException.class);
        }
    }

    @Nested
    @DisplayName("Validação de CNPJ")
    class CNPJValidationTests {

        @Test
        @DisplayName("Deve criar customer com CNPJ válido formatado")
        void shouldCreateCustomerWithValidFormattedCNPJ() {
            String validCNPJ = "11.222.333/0001-81";

            Customer customer = Customer.create(
                    "Empresa XYZ",
                    DocumentType.CNPJ,
                    validCNPJ,
                    "+55 (11) 3456-7890",
                    "empresa@example.com",
                    "Av. B, 456"
            );

            assertThat(customer).isNotNull();
            assertThat(customer.getDocumentNumber()).isEqualTo("11222333000181");
            assertThat(customer.getDocumentType()).isEqualTo(DocumentType.CNPJ);
        }

        @Test
        @DisplayName("Deve criar customer com CNPJ válido sem formatação")
        void shouldCreateCustomerWithValidUnformattedCNPJ() {
            String validCNPJ = "11222333000181";

            Customer customer = Customer.create(
                    "Empresa XYZ",
                    DocumentType.CNPJ,
                    validCNPJ,
                    "+55 (11) 3456-7890",
                    "empresa@example.com",
                    "Av. B, 456"
            );

            assertThat(customer).isNotNull();
            assertThat(customer.getDocumentNumber()).isEqualTo("11222333000181");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "11.111.111/0001-11",
                "00.000.000/0000-00",
                "11.222.333/0001-00",
                "11.222.333/001-81",
                "abcdefghijklmn",
                "123",
                ""
        })
        @DisplayName("Não deve criar customer com CNPJ inválido")
        void shouldThrowExceptionWithInvalidCNPJ(String invalidCNPJ) {
            assertThatThrownBy(() -> Customer.create(
                    "Empresa XYZ",
                    DocumentType.CNPJ,
                    invalidCNPJ,
                    "(11) 3456-7890",
                    "empresa@example.com",
                    "Av. B, 456"
            ))
                    .isInstanceOf(InvalidDocumentException.class)
                    .hasMessageContaining("CNPJ");
        }

        @Test
        @DisplayName("Não deve criar customer com CNPJ nulo")
        void shouldThrowExceptionWithNullCNPJ() {
            assertThatThrownBy(() -> Customer.create(
                    "Empresa XYZ",
                    DocumentType.CNPJ,
                    null,
                    "(11) 3456-7890",
                    "empresa@example.com",
                    "Av. B, 456"
            ))
                    .isInstanceOf(InvalidDocumentException.class);
        }

        @Test
        @DisplayName("Deve reconhecer CNPJ válido: 11.222.333/0001-81")
        void shouldValidateCorrectCNPJ() {
            assertThatCode(() -> Customer.create(
                    "Empresa",
                    DocumentType.CNPJ,
                    "11.222.333/0001-81",
                    "+55 (11) 98765-4321",
                    "empresa@example.com",
                    "Rua"
            )).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Deve rejeitar CNPJ inválido: 11.222.333/0001-00")
        void shouldRejectInvalidCNPJCheckDigits() {
            assertThatThrownBy(() -> Customer.create(
                    "Empresa",
                    DocumentType.CNPJ,
                    "11.222.333/0001-00",
                    "+55 (11) 98765-4321",
                    "empresa@example.com",
                    "Rua"
            ))
                    .isInstanceOf(InvalidDocumentException.class);
        }
    }

    @Nested
    @DisplayName("Validação de Telefone")
    class TelephoneValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "55(11) 98765-4321",
                "5511987654321",
                "55(11) 3456-7890",
                "551134567890",
                "+55 (11) 98765-4321",
                "+55 11 98765-4321",
        })
        @DisplayName("Deve criar customer com telefone válido")
        void shouldCreateCustomerWithValidTelephone(String validTelephone) {
            assertThatCode(() -> Customer.create(
                    "João Silva",
                    DocumentType.CPF,
                    "111.444.777-35",
                    validTelephone,
                    "joao@example.com",
                    "Rua A, 123"
            )).doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "123456789",
                "(11) 9876",
                "12",
                "",
                "123",
                "(11) 987654320",
        })
        @DisplayName("Não deve criar customer com telefone inválido (< 12 dígitos)")
        void shouldThrowExceptionWithInvalidTelephone(String invalidTelephone) {
            assertThatThrownBy(() -> Customer.create(
                    "João Silva",
                    DocumentType.CPF,
                    "111.444.777-35",
                    invalidTelephone,
                    "joao@example.com",
                    "Rua A, 123"
            ))
                    .isInstanceOf(InvalidTelephoneException.class)
                    .hasMessageContaining("inválido");
        }

        @Test
        @DisplayName("Não deve criar customer com telefone nulo")
        void shouldThrowExceptionWithNullTelephone() {
            assertThatThrownBy(() -> Customer.create(
                    "João Silva",
                    DocumentType.CPF,
                    "111.444.777-35",
                    null,
                    "joao@example.com",
                    "Rua A, 123"
            ))
                    .isInstanceOf(InvalidTelephoneException.class);
        }
    }

    @Nested
    @DisplayName("Atualização de Informações do Cliente")
    class CustomerUpdateTests {

        @Test
        @DisplayName("Deve atualizar informações mantendo o documento igual")
        void shouldUpdateWithSameDocument() {
            Customer customer = Customer.create(
                    "João Silva",
                    DocumentType.CPF,
                    "111.444.777-35",
                    "+55 (11) 98765-4321",
                    "joao@example.com",
                    "Rua A, 123"
            );

            String originalDocument = customer.getDocumentNumber();

            customer.update(
                    "João Silva Santos",
                    DocumentType.CPF,
                    "111.444.777-35", // Mesmo documento
                    "+55 (11) 99999-9999",
                    "joao.santos@example.com",
                    "Rua A, 456"
            );

            assertThat(customer.getName()).isEqualTo("João Silva Santos");
            assertThat(customer.getTelephone()).isEqualTo("5511999999999");
            assertThat(customer.getEmail()).isEqualTo("joao.santos@example.com");
            assertThat(customer.getAddress()).isEqualTo("Rua A, 456");
            assertThat(customer.getDocumentNumber()).isEqualTo(originalDocument);
        }

        @Test
        @DisplayName("Não deve permitir atualizar para CPF inválido")
        void shouldThrowExceptionWhenUpdatingWithInvalidCPF() {
            Customer customer = Customer.create(
                    "João Silva",
                    DocumentType.CPF,
                    "111.444.777-35",
                    "+55 (11) 98765-4321",
                    "joao@example.com",
                    "Rua A, 123"
            );

            assertThatThrownBy(() -> customer.update(
                    "João Silva",
                    DocumentType.CPF,
                    "111.111.111-11",
                    "+55 (11) 98765-4321",
                    "joao@example.com",
                    "Rua A, 123"
            ))
                    .isInstanceOf(InvalidDocumentException.class);
        }

        @Test
        @DisplayName("Não deve permitir atualizar para CNPJ inválido")
        void shouldThrowExceptionWhenUpdatingWithInvalidCNPJ() {
            Customer customer = Customer.create(
                    "Empresa",
                    DocumentType.CNPJ,
                    "11.222.333/0001-81",
                    "+55 (11) 98765-4321",
                    "empresa@example.com",
                    "Av. B, 456"
            );

            assertThatThrownBy(() -> customer.update(
                    "Empresa",
                    DocumentType.CNPJ,
                    "11.111.111/0001-11",
                    "+55 (11) 98765-4321",
                    "empresa@example.com",
                    "Av. B, 456"
            ))
                    .isInstanceOf(InvalidDocumentException.class);
        }

        @Test
        @DisplayName("Não deve permitir atualizar com telefone inválido")
        void shouldThrowExceptionWhenUpdatingWithInvalidTelephone() {
            Customer customer = Customer.create(
                    "João Silva",
                    DocumentType.CPF,
                    "111.444.777-35",
                    "+55 (11) 98765-4321",
                    "joao@example.com",
                    "Rua A, 123"
            );

            assertThatThrownBy(() -> customer.update(
                    "João Silva",
                    DocumentType.CPF,
                    "111.444.777-35",
                    "123",
                    "joao@example.com",
                    "Rua A, 123"
            ))
                    .isInstanceOf(InvalidTelephoneException.class);
        }
    }

}


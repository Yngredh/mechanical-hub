package com.fiap.mechanical_hub.domain.service;

import com.fiap.mechanical_hub.domain.exceptions.DuplicatedDocumentException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerDomainServiceTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final CustomerDomainService service = new CustomerDomainService(customerRepository);

    @Test
    void shouldNotThrowException_whenDocumentIsUnique() {
        Document document = new Document(DocumentTypeEnum.CPF, "12345678901");
        when(customerRepository.existsByDocumentNumber(document.getNumber())).thenReturn(false);

        assertThatCode(() -> service.validateUniqueDocument(document))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_whenDocumentAlreadyExists() {
        Document document = new Document(DocumentTypeEnum.CPF, "12345678901");
        when(customerRepository.existsByDocumentNumber(document.getNumber())).thenReturn(true);

        assertThatThrownBy(() -> service.validateUniqueDocument(document))
                .isInstanceOf(DuplicatedDocumentException.class)
                .hasMessageContaining("duplicado");
    }

    @Test
    void shouldValidateCpfDuplication() {
        Document cpfDocument = new Document(DocumentTypeEnum.CPF, "98765432100");
        when(customerRepository.existsByDocumentNumber("98765432100")).thenReturn(true);

        assertThatThrownBy(() -> service.validateUniqueDocument(cpfDocument))
                .isInstanceOf(DuplicatedDocumentException.class);
    }

    @Test
    void shouldValidateCnpjDuplication() {
        Document cnpjDocument = new Document(DocumentTypeEnum.CNPJ, "12345678901234");
        when(customerRepository.existsByDocumentNumber("12345678901234")).thenReturn(true);

        assertThatThrownBy(() -> service.validateUniqueDocument(cnpjDocument))
                .isInstanceOf(DuplicatedDocumentException.class);
    }

    @Test
    void shouldValidateMultipleDifferentDocuments() {
        Document document1 = new Document(DocumentTypeEnum.CPF, "11111111111");
        Document document2 = new Document(DocumentTypeEnum.CPF, "22222222222");

        when(customerRepository.existsByDocumentNumber("11111111111")).thenReturn(false);
        when(customerRepository.existsByDocumentNumber("22222222222")).thenReturn(false);

        assertThatCode(() -> {
            service.validateUniqueDocument(document1);
            service.validateUniqueDocument(document2);
        }).doesNotThrowAnyException();
    }
}
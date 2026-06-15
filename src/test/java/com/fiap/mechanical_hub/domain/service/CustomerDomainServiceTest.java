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

    private static final String VALID_CPF_1 = "52998224725";
    private static final String VALID_CPF_2 = "71428793860";
    private static final String VALID_CNPJ = "11222333000181";

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final CustomerDomainService service = new CustomerDomainService(customerRepository);

    @Test
    void shouldNotThrowException_whenDocumentIsUnique() {
        Document document = new Document(DocumentTypeEnum.CPF, VALID_CPF_1);
        when(customerRepository.existsByDocumentNumber(document.getNumber())).thenReturn(false);

        assertThatCode(() -> service.validateUniqueDocument(document))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_whenDocumentAlreadyExists() {
        Document document = new Document(DocumentTypeEnum.CPF, VALID_CPF_1);
        when(customerRepository.existsByDocumentNumber(document.getNumber())).thenReturn(true);

        assertThatThrownBy(() -> service.validateUniqueDocument(document))
                .isInstanceOf(DuplicatedDocumentException.class)
                .hasMessageContaining("duplicado");
    }

    @Test
    void shouldValidateCpfDuplication() {
        Document cpfDocument = new Document(DocumentTypeEnum.CPF, VALID_CPF_2);
        when(customerRepository.existsByDocumentNumber(VALID_CPF_2)).thenReturn(true);

        assertThatThrownBy(() -> service.validateUniqueDocument(cpfDocument))
                .isInstanceOf(DuplicatedDocumentException.class);
    }

    @Test
    void shouldValidateCnpjDuplication() {
        Document cnpjDocument = new Document(DocumentTypeEnum.CNPJ, VALID_CNPJ);
        when(customerRepository.existsByDocumentNumber(VALID_CNPJ)).thenReturn(true);

        assertThatThrownBy(() -> service.validateUniqueDocument(cnpjDocument))
                .isInstanceOf(DuplicatedDocumentException.class);
    }

    @Test
    void shouldValidateMultipleDifferentDocuments() {
        Document document1 = new Document(DocumentTypeEnum.CPF, VALID_CPF_1);
        Document document2 = new Document(DocumentTypeEnum.CPF, VALID_CPF_2);

        when(customerRepository.existsByDocumentNumber(VALID_CPF_1)).thenReturn(false);
        when(customerRepository.existsByDocumentNumber(VALID_CPF_2)).thenReturn(false);

        assertThatCode(() -> {
            service.validateUniqueDocument(document1);
            service.validateUniqueDocument(document2);
        }).doesNotThrowAnyException();
    }
}

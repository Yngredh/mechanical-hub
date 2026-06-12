package com.fiap.mechanical_hub.domain.utils.document;

import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidDocumentException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentValidatorTest {

    private static final String VALID_CPF = "52998224725";
    private static final String VALID_CPF_FORMATTED = "529.982.247-25";
    private static final String VALID_CNPJ = "11222333000181";
    private static final String VALID_CNPJ_FORMATTED = "11.222.333/0001-81";

    @Test
    void shouldNotThrow_whenCpfIsValid() {
        assertThatCode(() -> DocumentValidator.validateDocument(DocumentTypeEnum.CPF, VALID_CPF))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrow_whenCpfIsValidAndFormatted() {
        assertThatCode(() -> DocumentValidator.validateDocument(DocumentTypeEnum.CPF, VALID_CPF_FORMATTED))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrow_whenCnpjIsValid() {
        assertThatCode(() -> DocumentValidator.validateDocument(DocumentTypeEnum.CNPJ, VALID_CNPJ))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrow_whenCnpjIsValidAndFormatted() {
        assertThatCode(() -> DocumentValidator.validateDocument(DocumentTypeEnum.CNPJ, VALID_CNPJ_FORMATTED))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowInvalidDocumentException_whenCpfHasAllRepeatedDigits() {
        assertThatThrownBy(() -> DocumentValidator.validateDocument(DocumentTypeEnum.CPF, "11111111111"))
                .isInstanceOf(InvalidDocumentException.class);
    }

    @Test
    void shouldThrowInvalidDocumentException_whenCpfCheckDigitsAreWrong() {
        assertThatThrownBy(() -> DocumentValidator.validateDocument(DocumentTypeEnum.CPF, "12345678900"))
                .isInstanceOf(InvalidDocumentException.class);
    }

    @Test
    void shouldThrowInvalidDocumentException_whenCpfIsNull() {
        assertThatThrownBy(() -> DocumentValidator.validateDocument(DocumentTypeEnum.CPF, null))
                .isInstanceOf(InvalidDocumentException.class);
    }

    @Test
    void shouldThrowInvalidDocumentException_whenCpfIsTooShort() {
        assertThatThrownBy(() -> DocumentValidator.validateDocument(DocumentTypeEnum.CPF, "12345"))
                .isInstanceOf(InvalidDocumentException.class);
    }

    @Test
    void shouldThrowInvalidDocumentException_whenCnpjHasAllRepeatedDigits() {
        assertThatThrownBy(() -> DocumentValidator.validateDocument(DocumentTypeEnum.CNPJ, "00000000000000"))
                .isInstanceOf(InvalidDocumentException.class);
    }

    @Test
    void shouldThrowInvalidDocumentException_whenCnpjCheckDigitsAreWrong() {
        assertThatThrownBy(() -> DocumentValidator.validateDocument(DocumentTypeEnum.CNPJ, "12345678000100"))
                .isInstanceOf(InvalidDocumentException.class);
    }

    @Test
    void shouldThrowInvalidDocumentException_whenCnpjIsNull() {
        assertThatThrownBy(() -> DocumentValidator.validateDocument(DocumentTypeEnum.CNPJ, null))
                .isInstanceOf(InvalidDocumentException.class);
    }
}

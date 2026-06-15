package com.fiap.mechanical_hub.domain.utils.document;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentFormatterTest {

    private static final String VALID_CPF_DIGITS = "52998224725";
    private static final String VALID_CNPJ_DIGITS = "11222333000181";

    @Test
    void shouldFormatCpf_whenTypeIsCpfAndNumberHasElevenDigits() {
        String result = DocumentFormatter.formatDocument("CPF", VALID_CPF_DIGITS);

        assertThat(result).isEqualTo("529.982.247-25");
    }

    @Test
    void shouldFormatCnpj_whenTypeIsCnpjAndNumberHasFourteenDigits() {
        String result = DocumentFormatter.formatDocument("CNPJ", VALID_CNPJ_DIGITS);

        assertThat(result).isEqualTo("11.222.333/0001-81");
    }

    @Test
    void shouldReturnNumber_whenTypeIsNull() {
        String result = DocumentFormatter.formatDocument(null, VALID_CPF_DIGITS);

        assertThat(result).isEqualTo(VALID_CPF_DIGITS);
    }

    @Test
    void shouldReturnNull_whenNumberIsNull() {
        String result = null;

        assertThat(result).isNull();
    }

    @Test
    void shouldThrowException_whenCpfDoesNotHaveElevenDigits() {
        assertThatThrownBy(() -> DocumentFormatter.formatDocument("CPF", "12345"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("11 digits");
    }

    @Test
    void shouldThrowException_whenCnpjDoesNotHaveFourteenDigits() {
        assertThatThrownBy(() -> DocumentFormatter.formatDocument("CNPJ", "12345"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("14 digits");
    }
}

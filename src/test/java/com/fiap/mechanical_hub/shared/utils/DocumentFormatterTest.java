package com.fiap.mechanical_hub.shared.utils;

import com.fiap.mechanical_hub.domain.utils.document.DocumentFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class DocumentFormatterTest {

    @Test
    @DisplayName("Deve formatar CPF corretamente quando o tipo for CPF")
    void formatDocument_CPFSuccess() {
        String result = DocumentFormatter.formatDocument("CPF", "12345678901");
        assertEquals("123.456.789-01", result);
    }

    @Test
    @DisplayName("Deve formatar CPF corretamente ignorando case do tipo")
    void formatDocument_CPFCaseInsensitive() {
        String result = DocumentFormatter.formatDocument("cpf", "12345678901");
        assertEquals("123.456.789-01", result);
    }

    @Test
    @DisplayName("Deve formatar CNPJ corretamente quando o tipo não for CPF")
    void formatDocument_CNPJSuccess() {
        String result = DocumentFormatter.formatDocument("CNPJ", "12345678000199");
        assertEquals("12.345.678/0001-99", result);
    }

    @ParameterizedTest
    @CsvSource({
            "CPF, 123",
            "CNPJ, 12345"
    })
    @DisplayName("Deve lançar IllegalArgumentException quando o tamanho do documento for inválido")
    void formatDocument_InvalidLength(String type, String number) {
        assertThrows(IllegalArgumentException.class, () ->
                DocumentFormatter.formatDocument(type, number)
        );
    }

    @Test
    @DisplayName("Deve retornar o número original se o tipo ou o número forem nulos")
    void formatDocument_NullInputs() {
        assertNull(DocumentFormatter.formatDocument(null, null));
        assertEquals("123", DocumentFormatter.formatDocument(null, "123"));
        assertNull(DocumentFormatter.formatDocument("CPF", null));
    }

    @Test
    @DisplayName("Deve remover formatação existente antes de formatar novamente")
    void formatDocument_RemoveExistingFormatting() {
        // CPF já com alguns pontos/hifens mas em posições possivelmente erradas
        String result = DocumentFormatter.formatDocument("CPF", "123.456.789-01");
        assertEquals("123.456.789-01", result);

        // CNPJ
        String resultCnpj = DocumentFormatter.formatDocument("CNPJ", "12.345.678/0001-99");
        assertEquals("12.345.678/0001-99", resultCnpj);
    }

    @Test
    @DisplayName("Deve lançar exceção com mensagem específica para CPF curto")
    void formatCPF_ExceptionMessage() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                DocumentFormatter.formatDocument("CPF", "123")
        );
        assertEquals("CPF must have 11 digits", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção com mensagem específica para CNPJ curto")
    void formatCNPJ_ExceptionMessage() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                DocumentFormatter.formatDocument("CNPJ", "123")
        );
        assertEquals("CNPJ must have 14 digits", exception.getMessage());
    }

}
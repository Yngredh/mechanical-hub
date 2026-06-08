package com.fiap.mechanical_hub.domain.valueobjects;

import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentTest {

    private static final String VALID_CPF = "52998224725";
    private static final String VALID_CPF_FORMATTED = "529.982.247-25";
    private static final String VALID_CNPJ = "11222333000181";
    private static final String VALID_CNPJ_FORMATTED = "11.222.333/0001-81";

    @Test
    void shouldCreateValidCpf_whenFormatIsCorrect() {
        Document document = new Document(DocumentTypeEnum.CPF, VALID_CPF);

        assertThat(document.getType()).isEqualTo(DocumentTypeEnum.CPF);
        assertThat(document.getNumber()).isNotNull();
    }

    @Test
    void shouldCreateValidCnpj_whenFormatIsCorrect() {
        Document document = new Document(DocumentTypeEnum.CNPJ, VALID_CNPJ);

        assertThat(document.getType()).isEqualTo(DocumentTypeEnum.CNPJ);
        assertThat(document.getNumber()).isNotNull();
    }

    @Test
    void shouldThrowException_whenCpfIsInvalid() {
        assertThatThrownBy(() -> new Document(DocumentTypeEnum.CPF, "11111111111"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldThrowException_whenCnpjIsInvalid() {
        assertThatThrownBy(() -> new Document(DocumentTypeEnum.CNPJ, "00000000000000"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldThrowException_whenDocumentNumberIsBlank() {
        assertThatThrownBy(() -> new Document(DocumentTypeEnum.CPF, ""))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldRemoveFormattingFromCpf_whenCreatingDocument() {
        Document document = new Document(DocumentTypeEnum.CPF, VALID_CPF_FORMATTED);

        assertThat(document.getNumber()).doesNotContain(".");
        assertThat(document.getNumber()).doesNotContain("-");
    }

    @Test
    void shouldRemoveFormattingFromCnpj_whenCreatingDocument() {
        Document document = new Document(DocumentTypeEnum.CNPJ, VALID_CNPJ_FORMATTED);

        assertThat(document.getNumber()).doesNotContain(".");
        assertThat(document.getNumber()).doesNotContain("/");
        assertThat(document.getNumber()).doesNotContain("-");
    }
}

package com.fiap.mechanical_hub.domain.valueobjects;

import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import lombok.Getter;

import static com.fiap.mechanical_hub.domain.utils.document.DocumentValidator.validateDocument;

@Getter
public class Document {

    private final DocumentTypeEnum type;
    private final String number;

    public Document(DocumentTypeEnum type, String number) {

        validateDocument(type, number);

        this.type = type;
        this.number = removeFormatting(number);
    }

    public static String removeFormatting(String document) {
        if (document == null) { return null; }
        return document.replaceAll("\\D", "");
    }

}

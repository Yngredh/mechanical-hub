package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public enum LicensePlatePatternEnum {
    ANTIGO("Antigo", "[A-Z]{3}\\d{4}"),
    MERCOSUL("Mercosul", "[A-Z]{3}\\d[A-Z]\\d{2}");

    private final String description;
    private final String regex;

    LicensePlatePatternEnum(String description, String regex) {
        this.description = description;
        this.regex = regex;
    }

    public boolean matches(String licensePlate) {
        return Pattern.matches(this.regex, licensePlate);
    }
}


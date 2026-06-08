package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

@Getter
public enum ProfileEnum {
    MECHANICAL("MECANICO", "Mecânico"),
    ADMINISTRATOR("ADMIN", "Administrador");

    private final String displayName;
    private final String description;

    ProfileEnum(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}

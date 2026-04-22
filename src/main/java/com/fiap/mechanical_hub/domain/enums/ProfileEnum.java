package com.fiap.mechanical_hub.domain.enums;

import lombok.Getter;

@Getter
public enum ProfileEnum {
    MECHANICAL("Mecânico"),
    ADMINISTRATOR("Administrador");

    ProfileEnum(String description) {
    }
}

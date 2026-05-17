package com.fiap.mechanical_hub.domain.valueobjects;

import static com.fiap.mechanical_hub.domain.utils.license_plate.LicensePlateFormatter.normalize;
import static com.fiap.mechanical_hub.domain.utils.license_plate.LicensePlateValidator.validateLicensePlate;

public class LicensePlate {

    private final String value;

    public LicensePlate(String licensePlate) {
        validateLicensePlate(licensePlate);
        this.value = normalize(licensePlate);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LicensePlate that = (LicensePlate) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}


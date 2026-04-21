package com.fiap.mechanical_hub.shared.utils.license_plate;

public class LicensePlateFormatter {

    private LicensePlateFormatter() {}

    public static String normalize(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return null;
        }
        return licensePlate
                .trim()
                .toUpperCase()
                .replace("-", "");
    }
}


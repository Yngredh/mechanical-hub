package com.fiap.mechanical_hub.shared.utils;

public class TelephoneFormatter {

    private TelephoneFormatter() {}

    public static String removeFormatting(String telephone) {
        if (telephone == null) { return null; }
        return telephone.replaceAll("\\D", "");
    }

    public static String format(String telephone) {
        if (telephone == null) { return null; }

        String cleanTelephone = removeFormatting(telephone);

        return switch (cleanTelephone.length()) {
            case 10 -> formatLandline(cleanTelephone);
            case 11 -> formatMobile(cleanTelephone);
            case 12 -> formatWithCountryCode10Digits(cleanTelephone);
            case 13 -> formatWithCountryCode11Digits(cleanTelephone);
            default -> throw new IllegalArgumentException("Invalid telephone format: must have 10, 11, 12 or 13 digits");
        };
    }

    private static String formatLandline(String telephone) {
        return String.format("(%s) %s-%s",
                telephone.substring(0, 2),
                telephone.substring(2, 6),
                telephone.substring(6, 10));
    }

    private static String formatMobile(String telephone) {
        return String.format("(%s) %s %s-%s",
                telephone.substring(0, 2),
                telephone.substring(2, 3),
                telephone.substring(3, 7),
                telephone.substring(7, 11));
    }

    private static String formatWithCountryCode10Digits(String telephone) {
        return String.format("+55 (%s) %s-%s",
                telephone.substring(2, 4),
                telephone.substring(4, 8),
                telephone.substring(8, 12));
    }

    private static String formatWithCountryCode11Digits(String telephone) {
        return String.format("+55 (%s) %s %s-%s",
                telephone.substring(2, 4),
                telephone.substring(4, 5),
                telephone.substring(5, 9),
                telephone.substring(9, 13));
    }
}


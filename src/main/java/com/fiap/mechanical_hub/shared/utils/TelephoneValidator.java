package com.fiap.mechanical_hub.shared.utils;


public class TelephoneValidator {

    private TelephoneValidator() {}

    public static boolean isValid(String telephone) {
        if (telephone == null) {
            return false;
        }

        String cleanTelephone = TelephoneFormatter.removeFormatting(telephone);

        return cleanTelephone.length() >= 12;
    }
}


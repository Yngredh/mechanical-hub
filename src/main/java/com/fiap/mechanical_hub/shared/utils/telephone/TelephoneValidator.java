package com.fiap.mechanical_hub.shared.utils.telephone;

import com.fiap.mechanical_hub.domain.exceptions.InvalidTelephoneException;

import static com.fiap.mechanical_hub.shared.utils.Formatter.removeFormatting;


public class TelephoneValidator {

    private TelephoneValidator() {}

    public static void validateTelephone(String telephone) {
        if (!isValidTelephone(telephone)) {
            throw new InvalidTelephoneException(
                    "Telefone inválido: após remover a formatação, deve conter pelo menos 12 dígitos"
            );
        }
    }


    public static boolean isValidTelephone(String telephone) {
        if (telephone == null) { return false; }

        String cleanTelephone = removeFormatting(telephone);
        return cleanTelephone.length() >= 12;
    }

}

package com.fiap.mechanical_hub.shared.utils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DocumentValidator {

    public DocumentValidator(String document) {
    }

    public static boolean isValidCPF(String cpf) {
        if (cpf == null) { return false; }

        // Remove formatting
        String cleanCpf = cpf.replaceAll("\\D", "");

        // Must have exactly 11 digits
        if (cleanCpf.length() != 11) { return false; }

        // Check if all digits are the same (invalid CPF)
        if (cleanCpf.matches("(\\d)\\1{10}")) { return false; }

        // Calculate first check digit
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cleanCpf.charAt(i) - '0') * (10 - i);
        }
        int firstCheckDigit = 11 - (sum % 11);
        if (firstCheckDigit >= 10) { firstCheckDigit = 0; }

        if ((cleanCpf.charAt(9) - '0') != firstCheckDigit) { return false; }

        // Calculate second check digit
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cleanCpf.charAt(i) - '0') * (11 - i);
        }
        int secondCheckDigit = 11 - (sum % 11);
        if (secondCheckDigit >= 10) {
            secondCheckDigit = 0;
        }

        return (cleanCpf.charAt(10) - '0') == secondCheckDigit;
    }

    /**
     * Validates CNPJ format and check digits.
     * CNPJ format: XX.XXX.XXX/XXXX-XX or XXXXXXXXXXXXXX
     *
     * @param cnpj the CNPJ to validate (with or without formatting)
     * @return true if valid, false otherwise
     */
    public static boolean isValidCNPJ(String cnpj) {
        if (cnpj == null) {
            return false;
        }

        // Remove formatting
        String cleanCnpj = cnpj.replaceAll("\\D", "");

        // Must have exactly 14 digits
        if (cleanCnpj.length() != 14) {
            return false;
        }

        // Check if all digits are the same (invalid CNPJ)
        if (cleanCnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Calculate first check digit
        int sum = 0;
        int multiplier = 5;
        for (int i = 0; i < 8; i++) {
            sum += (cleanCnpj.charAt(i) - '0') * multiplier;
            multiplier--;
            if (multiplier == 1) {
                multiplier = 9;
            }
        }

        sum += (cleanCnpj.charAt(8) - '0') * 2;
        for (int i = 9; i < 12; i++) {
            sum += (cleanCnpj.charAt(i) - '0') * multiplier;
            multiplier--;
            if (multiplier == 1) {
                multiplier = 9;
            }
        }

        int firstCheckDigit = 11 - (sum % 11);
        if (firstCheckDigit >= 10) {
            firstCheckDigit = 0;
        }

        if ((cleanCnpj.charAt(12) - '0') != firstCheckDigit) {
            return false;
        }

        // Calculate second check digit
        sum = 0;
        multiplier = 6;
        for (int i = 0; i < 9; i++) {
            sum += (cleanCnpj.charAt(i) - '0') * multiplier;
            multiplier--;
            if (multiplier == 1) {
                multiplier = 9;
            }
        }

        for (int i = 9; i < 13; i++) {
            sum += (cleanCnpj.charAt(i) - '0') * multiplier;
            multiplier--;
            if (multiplier == 1) {
                multiplier = 9;
            }
        }

        int secondCheckDigit = 11 - (sum % 11);
        if (secondCheckDigit >= 10) {
            secondCheckDigit = 0;
        }

        return (cleanCnpj.charAt(13) - '0') == secondCheckDigit;
    }
}


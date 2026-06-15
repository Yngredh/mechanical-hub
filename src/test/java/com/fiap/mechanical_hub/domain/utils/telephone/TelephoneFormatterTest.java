package com.fiap.mechanical_hub.domain.utils.telephone;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TelephoneFormatterTest {

    @Test
    void shouldFormatAsLandline_whenTelephoneHasTenDigits() {
        String result = TelephoneFormatter.formatTelephone("1134567890");

        assertThat(result).isEqualTo("(11) 3456-7890");
    }

    @Test
    void shouldFormatAsMobile_whenTelephoneHasElevenDigits() {
        String result = TelephoneFormatter.formatTelephone("11987654321");

        assertThat(result).isEqualTo("(11) 9 8765-4321");
    }

    @Test
    void shouldFormatWithCountryCode_whenTelephoneHasTwelveDigits() {
        String result = TelephoneFormatter.formatTelephone("551134567890");

        assertThat(result).isEqualTo("+55 (11) 3456-7890");
    }

    @Test
    void shouldFormatWithCountryCodeAndNineDigit_whenTelephoneHasThirteenDigits() {
        String result = TelephoneFormatter.formatTelephone("5511987654321");

        assertThat(result).isEqualTo("+55 (11) 9 8765-4321");
    }

    @Test
    void shouldReturnNull_whenTelephoneIsNull() {
        String result = TelephoneFormatter.formatTelephone(null);

        assertThat(result).isNull();
    }

    @Test
    void shouldThrowException_whenTelephoneDigitCountIsUnsupported() {
        assertThatThrownBy(() -> TelephoneFormatter.formatTelephone("123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Telefone inválido");
    }
}

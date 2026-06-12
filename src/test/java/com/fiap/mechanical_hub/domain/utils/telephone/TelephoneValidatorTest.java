package com.fiap.mechanical_hub.domain.utils.telephone;

import com.fiap.mechanical_hub.domain.exceptions.InvalidTelephoneException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TelephoneValidatorTest {

    private static final String VALID_TELEPHONE_WITH_COUNTRY_CODE = "5511987654321";

    @Test
    void shouldNotThrow_whenTelephoneHasTwelveOrMoreDigits() {
        assertThatCode(() -> TelephoneValidator.validateTelephone(VALID_TELEPHONE_WITH_COUNTRY_CODE))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowInvalidTelephoneException_whenTelephoneIsNull() {
        assertThatThrownBy(() -> TelephoneValidator.validateTelephone(null))
                .isInstanceOf(InvalidTelephoneException.class)
                .hasMessageContaining("Telefone inválido");
    }

    @Test
    void shouldThrowInvalidTelephoneException_whenTelephoneHasFewerThanTwelveDigits() {
        assertThatThrownBy(() -> TelephoneValidator.validateTelephone("1198765"))
                .isInstanceOf(InvalidTelephoneException.class)
                .hasMessageContaining("Telefone inválido");
    }

    @Test
    void shouldReturnTrue_whenTelephoneIsValid() {
        boolean result = TelephoneValidator.isValidTelephone(VALID_TELEPHONE_WITH_COUNTRY_CODE);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalse_whenTelephoneIsNull() {
        boolean result = TelephoneValidator.isValidTelephone(null);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalse_whenTelephoneHasFewerThanTwelveDigits() {
        boolean result = TelephoneValidator.isValidTelephone("1198765");

        assertThat(result).isFalse();
    }
}

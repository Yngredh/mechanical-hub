package com.fiap.mechanical_hub.domain.utils.license_plate;

import com.fiap.mechanical_hub.domain.exceptions.InvalidLicensePlateException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LicensePlateValidatorTest {

    @Test
    void shouldNotThrow_whenLicensePlateMatchesOldStandard() {
        assertThatCode(() -> LicensePlateValidator.validateLicensePlate("ABC1234"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrow_whenLicensePlateMatchesMercosulStandard() {
        assertThatCode(() -> LicensePlateValidator.validateLicensePlate("ABC1D23"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrow_whenLicensePlateIsFormattedWithDash() {
        assertThatCode(() -> LicensePlateValidator.validateLicensePlate("ABC-1234"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowInvalidLicensePlateException_whenFormatIsInvalid() {
        assertThatThrownBy(() -> LicensePlateValidator.validateLicensePlate("INVALID"))
                .isInstanceOf(InvalidLicensePlateException.class)
                .hasMessageContaining("Formato de placa inválido");
    }

    @Test
    void shouldThrowInvalidLicensePlateException_whenLicensePlateIsBlank() {
        assertThatThrownBy(() -> LicensePlateValidator.validateLicensePlate(""))
                .isInstanceOf(InvalidLicensePlateException.class)
                .hasMessageContaining("Placa vazia");
    }

    @Test
    void shouldThrowInvalidLicensePlateException_whenLicensePlateIsNull() {
        assertThatThrownBy(() -> LicensePlateValidator.validateLicensePlate(null))
                .isInstanceOf(InvalidLicensePlateException.class)
                .hasMessageContaining("Placa vazia");
    }
}

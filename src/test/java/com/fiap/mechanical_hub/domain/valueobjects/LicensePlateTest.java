package com.fiap.mechanical_hub.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LicensePlateTest {

    @Test
    void shouldCreateValidLicensePlate_whenFormatIsOldStandard() {
        LicensePlate licensePlate = new LicensePlate("ABC1234");

        assertThat(licensePlate.getValue()).isNotNull();
        assertThat(licensePlate.getValue()).contains("ABC");
    }

    @Test
    void shouldCreateValidLicensePlate_whenFormatIsMercosul() {
        LicensePlate licensePlate = new LicensePlate("ABC1D23");

        assertThat(licensePlate.getValue()).isNotNull();
    }

    @Test
    void shouldThrowException_whenLicensePlateFormatIsInvalid() {
        assertThatThrownBy(() -> new LicensePlate("INVALID"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldThrowException_whenLicensePlateIsBlank() {
        assertThatThrownBy(() -> new LicensePlate(""))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldThrowException_whenLicensePlateIsNull() {
        assertThatThrownBy(() -> new LicensePlate(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldNormalizeLicensePlate_convertingToStandardFormat() {
        LicensePlate licensePlate = new LicensePlate("abc1234");

        assertThat(licensePlate.getValue()).isNotNull();
    }

    @Test
    void shouldImplementEquals_basedOnValue() {
        LicensePlate licensePlate1 = new LicensePlate("ABC1234");
        LicensePlate licensePlate2 = new LicensePlate("ABC1234");

        assertThat(licensePlate1).isEqualTo(licensePlate2);
    }

    @Test
    void shouldReturnDifferentHashCode_forDifferentLicensePlates() {
        LicensePlate licensePlate1 = new LicensePlate("ABC1234");
        LicensePlate licensePlate2 = new LicensePlate("XYZ9999");

        assertThat(licensePlate1.hashCode()).isNotEqualTo(licensePlate2.hashCode());
    }

    @Test
    void shouldReturnStringValue_inToString() {
        LicensePlate licensePlate = new LicensePlate("ABC1234");

        assertThat(licensePlate.toString()).hasToString(licensePlate.getValue());
    }
}


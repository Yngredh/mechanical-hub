package com.fiap.mechanical_hub.domain.utils.license_plate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LicensePlateFormatterTest {

    @Test
    void shouldNormalizeToUpperCase_whenLicensePlateIsLowercase() {
        String result = LicensePlateFormatter.normalize("abc1234");

        assertThat(result).isEqualTo("ABC1234");
    }

    @Test
    void shouldRemoveDash_whenLicensePlateContainsDash() {
        String result = LicensePlateFormatter.normalize("ABC-1234");

        assertThat(result).isEqualTo("ABC1234");
    }

    @Test
    void shouldTrimWhitespace_whenLicensePlateHasLeadingOrTrailingSpaces() {
        String result = LicensePlateFormatter.normalize("  ABC1234  ");

        assertThat(result).isEqualTo("ABC1234");
    }

    @Test
    void shouldReturnNull_whenLicensePlateIsNull() {
        String result = LicensePlateFormatter.normalize(null);

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnNull_whenLicensePlateIsBlank() {
        String result = LicensePlateFormatter.normalize("   ");

        assertThat(result).isNull();
    }
}

package com.fiap.mechanical_hub.domain.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormatterTest {

    @Test
    void shouldRemoveNonDigitCharacters_whenStringContainsSpecialChars() {
        String result = Formatter.removeFormatting("529.982.247-25");

        assertThat(result).isEqualTo("52998224725");
    }

    @Test
    void shouldReturnOnlyDigits_whenStringContainsMixedCharacters() {
        String result = Formatter.removeFormatting("(11) 9 8765-4321");

        assertThat(result).isEqualTo("11987654321");
    }

    @Test
    void shouldReturnSameString_whenStringContainsOnlyDigits() {
        String result = Formatter.removeFormatting("12345678901");

        assertThat(result).isEqualTo("12345678901");
    }

    @Test
    void shouldReturnNull_whenInputIsNull() {
        String result = Formatter.removeFormatting(null);

        assertThat(result).isNull();
    }
}

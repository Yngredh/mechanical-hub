package com.fiap.mechanical_hub.domain.utils.telephone;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelephoneFormatterTest {

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertNull(TelephoneFormatter.formatTelephone(null));
    }

    @Test
    void shouldFormatLandline10Digits() {
        String input = "1123456789";
        String expected = "(11) 2345-6789";

        assertEquals(expected, TelephoneFormatter.formatTelephone(input));
    }

    @Test
    void shouldFormatMobile11Digits() {
        String input = "11923456789";
        String expected = "(11) 9 2345-6789";

        assertEquals(expected, TelephoneFormatter.formatTelephone(input));
    }

    @Test
    void shouldFormatWithCountryCode12Digits() {
        String input = "551123456789";
        String expected = "+55 (11) 2345-6789";

        assertEquals(expected, TelephoneFormatter.formatTelephone(input));
    }

    @Test
    void shouldFormatWithCountryCode13Digits() {
        String input = "5511923456789";
        String expected = "+55 (11) 9 2345-6789";

        assertEquals(expected, TelephoneFormatter.formatTelephone(input));
    }

    @Test
    void shouldHandleAlreadyFormattedInputs() {
        assertEquals("(11) 2345-6789", TelephoneFormatter.formatTelephone("(11)2345-6789"));
        assertEquals("(11) 9 2345-6789", TelephoneFormatter.formatTelephone("(11) 9 2345-6789"));
        assertEquals("+55 (11) 9 2345-6789", TelephoneFormatter.formatTelephone("+55 (11) 9 2345-6789"));
    }

    @Test
    void shouldThrowWhenInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> TelephoneFormatter.formatTelephone("123"));
        assertThrows(IllegalArgumentException.class, () -> TelephoneFormatter.formatTelephone("12345678901234"));
    }

}


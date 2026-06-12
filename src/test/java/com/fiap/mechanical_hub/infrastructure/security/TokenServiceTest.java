package com.fiap.mechanical_hub.infrastructure.security;

import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.mocks.domain.entities.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private static final String SECRET = "test-secret-key-for-unit-tests-only";

    private final TokenService tokenService = new TokenService();

    @BeforeEach
    void setUp() throws Exception {
        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(tokenService, SECRET);
    }

    @Test
    void shouldGenerateNonNullToken_whenUserIsValid() {
        User user = UserMock.active();

        String token = tokenService.generateToken(user);

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void shouldReturnUserEmail_whenTokenIsValid() {
        User user = UserMock.active();
        String token = tokenService.generateToken(user);

        String subject = tokenService.validateToken(token);

        assertThat(subject).isEqualTo(user.getEmail());
    }

    @Test
    void shouldReturnEmptyString_whenTokenIsInvalid() {
        String subject = tokenService.validateToken("invalid.token.value");

        assertThat(subject).isEmpty();
    }

    @Test
    void shouldReturnEmptyString_whenTokenIsSignedWithDifferentSecret() throws Exception {
        TokenService otherService = new TokenService();
        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(otherService, "different-secret");

        User user = UserMock.active();
        String tokenFromOtherSecret = otherService.generateToken(user);

        String subject = tokenService.validateToken(tokenFromOtherSecret);

        assertThat(subject).isEmpty();
    }
}

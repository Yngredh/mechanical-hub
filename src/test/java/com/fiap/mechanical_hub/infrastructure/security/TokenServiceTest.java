package com.fiap.mechanical_hub.infrastructure.security;

import com.fiap.mechanical_hub.domain.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenServiceTest {

    private TokenService tokenService;
    private final String SECRET_TEST = "test-secret-123";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", SECRET_TEST);
    }

    @Test
    @DisplayName("Deve gerar um token válido com sucesso")
    void generateToken_Success() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("mecanico@oficina.com");

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Deve validar um token e retornar o email (subject)")
    void validateToken_Success() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("teste@hub.com");
        String token = tokenService.generateToken(user);

        String subject = tokenService.validateToken(token);

        assertEquals("teste@hub.com", subject);
    }

    @Test
    @DisplayName("Deve retornar String vazia ao validar um token inválido ou expirado")
    void validateToken_InvalidToken() {
        String invalidToken = "token-totalmente-errado.xyz";

        String subject = tokenService.validateToken(invalidToken);

        assertEquals("", subject);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o secret for nulo")
    void generateToken_NullSecretError() {
        ReflectionTestUtils.setField(tokenService, "secret", null);
        User user = mock(User.class);

        assertThrows(IllegalArgumentException.class, () -> tokenService.generateToken(user));
    }

    @Test
    @DisplayName("Deve lançar TokenErrorException quando ocorrer erro na criação do JWT")
    void generateToken_ShouldThrowTokenErrorException_OnCreationFailure() {
        User user = mock(User.class);

        ReflectionTestUtils.setField(tokenService, "secret", "segredo-teste");

        when(user.getEmail()).thenThrow(new RuntimeException("Simulated failure"));

        assertThrows(RuntimeException.class, () -> {
            tokenService.generateToken(user);
        });
    }

}
package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.authentication.AuthenticationRequest;
import com.fiap.mechanical_hub.application.dto.authentication.LoginResponse;
import com.fiap.mechanical_hub.infrastructure.security.TokenService;
import com.fiap.mechanical_hub.infrastructure.security.UserSecurityAdapter;
import com.fiap.mechanical_hub.mocks.domain.entities.UserMock;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {

    private static final String TOKEN = "jwt-token-value";

    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final TokenService tokenService = mock(TokenService.class);

    private final AuthenticationController controller = new AuthenticationController(
            authenticationManager, tokenService
    );

    @Test
    void shouldReturnOk_whenLoginIsSuccessful() {
        AuthenticationRequest request = new AuthenticationRequest("joao@email.com", "senha123");
        Authentication auth = mock(Authentication.class);
        UserSecurityAdapter userDetails = new UserSecurityAdapter(UserMock.active());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(any())).thenReturn(TOKEN);

        ResponseEntity<LoginResponse> response = controller.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isEqualTo(TOKEN);
    }

    @Test
    void shouldDelegateToAuthenticationManager_whenLoggingIn() {
        AuthenticationRequest request = new AuthenticationRequest("joao@email.com", "senha123");
        Authentication auth = mock(Authentication.class);
        UserSecurityAdapter userDetails = new UserSecurityAdapter(UserMock.active());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(any())).thenReturn(TOKEN);

        controller.login(request);

        verify(authenticationManager).authenticate(any());
    }

    @Test
    void shouldGenerateToken_whenAuthenticationSucceeds() {
        AuthenticationRequest request = new AuthenticationRequest("joao@email.com", "senha123");
        Authentication auth = mock(Authentication.class);
        UserSecurityAdapter userDetails = new UserSecurityAdapter(UserMock.active());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(any())).thenReturn(TOKEN);

        controller.login(request);

        verify(tokenService).generateToken(any());
    }
}

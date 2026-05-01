package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mechanical_hub.application.dto.authentication.AuthenticationRequest;
import com.fiap.mechanical_hub.application.dto.authentication.RegisterRequest;
import com.fiap.mechanical_hub.application.usecases.AuthorizationUseCase;
import com.fiap.mechanical_hub.domain.entities.Profile;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import com.fiap.mechanical_hub.infrastructure.security.SecurityConfiguration;
import com.fiap.mechanical_hub.infrastructure.security.TokenService;
import com.fiap.mechanical_hub.infrastructure.security.UserSecurityAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfiguration.class)
@DisplayName("AuthenticationController")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private AuthorizationUseCase authorizationUseCase;

    @MockBean
    private UserJpaRepository userJpaRepository;

    private AuthenticationRequest authenticationRequest;
    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        authenticationRequest = new AuthenticationRequest("admin@example.com", "senha123");
        registerRequest = new RegisterRequest(
                "Novo Usuario",
                "novo@example.com",
                "senha123",
                "ADMINISTRATOR"
        );

        Profile profile = Profile.create(ProfileEnum.ADMINISTRATOR);
        user = User.build(
                UUID.randomUUID(),
                "Administrador",
                "admin@example.com",
                "hash",
                profile
        );
    }

    @Nested
    @DisplayName("POST /auth/login")
    class Login {

        @Test
        @DisplayName("Should return 200 with token when credentials are valid")
        void shouldReturn200WithTokenWhenCredentialsAreValid() throws Exception {
            Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(new UserSecurityAdapter(user));
            when(tokenService.generateToken(user)).thenReturn("jwt-token");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authenticationRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token"));

            verify(authenticationManager).authenticate(any());
            verify(tokenService).generateToken(user);
        }

        @Test
        @DisplayName("Should return 403 when credentials are invalid")
        void shouldReturn403WhenCredentialsAreInvalid() throws Exception {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authenticationRequest)))
                    .andExpect(status().isForbidden());

            verify(authenticationManager).authenticate(any());
        }
    }

    @Nested
    @DisplayName("POST /auth/register")
    class Register {

        @Test
        @DisplayName("Should return 200 when user is registered")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenRegistered() throws Exception {
            doNothing().when(authorizationUseCase).registerNewUser(registerRequest);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isOk());

            verify(authorizationUseCase).registerNewUser(registerRequest);
        }

        @Test
        @DisplayName("Should return 400 when registration data is invalid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn400WhenRegistrationFails() throws Exception {
            doThrow(new IllegalArgumentException("User already exists"))
                    .when(authorizationUseCase).registerNewUser(registerRequest);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("User already exists"));
        }

        @Test
        @DisplayName("Should return 400 when request payload is invalid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn400WhenPayloadIsInvalid() throws Exception {
            RegisterRequest invalidRequest = new RegisterRequest(
                    "",
                    "not-a-valid-email",
                    "123",
                    ""
            );

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(authorizationUseCase);
        }

        @Test
        @DisplayName("Should return 403 when user is not authenticated")
        void shouldReturn403WhenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(authorizationUseCase);
        }

        @Test
        @DisplayName("Should return 403 when user is not administrator")
        @WithMockUser(roles = "MECHANICAL")
        void shouldReturn403WhenNotAdministrator() throws Exception {
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(authorizationUseCase);
        }
    }
}

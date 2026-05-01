package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.authentication.RegisterRequest;
import com.fiap.mechanical_hub.domain.entities.Profile;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.ProfileModel;
import com.fiap.mechanical_hub.infrastructure.database.models.UserModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ProfileJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import com.fiap.mechanical_hub.infrastructure.security.UserSecurityAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AuthorizationUseCase")
class AuthorizationUseCaseTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private ProfileJpaRepository profileRepository;

    @InjectMocks
    private AuthorizationUseCase authorizationUseCase;

    private RegisterRequest registerRequest;
    private UserModel userModel;
    private ProfileModel profileModel;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("João Silva", "user@example.com", "password123", "MECHANICAL");

        profileModel = new ProfileModel(
                UUID.randomUUID(),
                "MECHANICAL",
                "MECHANICAL"
        );

        userModel = new UserModel(
                UUID.randomUUID(),
                "João Silva",
                "user@example.com",
                "hashedPassword",
                profileModel
        );


        user = User.create(
                "João Silva",
                "user@example.com",
                "hashedPassword",
                Profile.create(ProfileEnum.MECHANICAL)
        );
    }

    @Test
    @DisplayName("Deve carregar usuário por email com sucesso")
    void shouldLoadUserByUsernameSuccessfully() {
        String email = "user@example.com";
        when(userRepository.findByEmail(email)).thenReturn(userModel);

        try (MockedStatic<com.fiap.mechanical_hub.application.mappers.UserMapper> userMapperMock = mockStatic(com.fiap.mechanical_hub.application.mappers.UserMapper.class)) {
            userMapperMock.when(() -> com.fiap.mechanical_hub.application.mappers.UserMapper.toDomain(userModel)).thenReturn(user);

            var result = authorizationUseCase.loadUserByUsername(email);

            assertNotNull(result);
            assertInstanceOf(UserSecurityAdapter.class, result);
            verify(userRepository).findByEmail(email);
            userMapperMock.verify(() -> com.fiap.mechanical_hub.application.mappers.UserMapper.toDomain(userModel));
        }
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário não encontrado")
    void shouldThrowUsernameNotFoundWhenUserNotFound() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> authorizationUseCase.loadUserByUsername(email));
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Deve registrar novo usuário com sucesso")
    void shouldRegisterNewUserSuccessfully() {
        when(userRepository.findByEmail(registerRequest.login())).thenReturn(null);
        when(profileRepository.findByName(registerRequest.profile())).thenReturn(profileModel);

        try (MockedStatic<com.fiap.mechanical_hub.application.mappers.UserMapper> userMapperMock = mockStatic(com.fiap.mechanical_hub.application.mappers.UserMapper.class);
             MockedConstruction<BCryptPasswordEncoder> encoderConstruction = mockConstruction(BCryptPasswordEncoder.class,
                     (mock, context) -> when(mock.encode(anyString())).thenReturn("hashedPassword"))) {

            userMapperMock.when(() -> com.fiap.mechanical_hub.application.mappers.UserMapper.toModel(any(User.class), eq(profileModel))).thenReturn(userModel);

            assertDoesNotThrow(() -> authorizationUseCase.registerNewUser(registerRequest));

            verify(userRepository).findByEmail(registerRequest.login());
            verify(profileRepository).findByName(registerRequest.profile());
            verify(userRepository).save(any(UserModel.class));
        }
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando usuário já existe")
    void shouldThrowIllegalArgumentWhenUserAlreadyExists() {
        when(userRepository.findByEmail(registerRequest.login())).thenReturn(userModel);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authorizationUseCase.registerNewUser(registerRequest));
        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository).findByEmail(registerRequest.login());
        verify(profileRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando perfil não encontrado")
    void shouldThrowIllegalArgumentWhenProfileNotFound() {
        when(userRepository.findByEmail(registerRequest.login())).thenReturn(null);
        when(profileRepository.findByName(registerRequest.profile())).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authorizationUseCase.registerNewUser(registerRequest));
        assertEquals("Profile not found: " + registerRequest.profile(), exception.getMessage());
        verify(userRepository).findByEmail(registerRequest.login());
        verify(profileRepository).findByName(registerRequest.profile());
        verify(userRepository, never()).save(any());
    }
}

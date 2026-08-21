package com.fiap.mechanical_hub.infrastructure.service;

import com.fiap.mechanical_hub.application.dto.authentication.RegisterRequest;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.exceptions.DuplicatedDocumentException;
import com.fiap.mechanical_hub.domain.exceptions.UserNotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.adapter.UserRepositoryAdapter;
import com.fiap.mechanical_hub.infrastructure.database.models.ProfileModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ProfileJpaRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.UserMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");
    private static final String DOCUMENT_NUMBER = "52998224725";

    private final ProfileJpaRepository profileRepository = mock(ProfileJpaRepository.class);
    private final UserRepositoryAdapter userRepositoryAdapter = mock(UserRepositoryAdapter.class);
    private final UserService userService = new UserService(profileRepository, userRepositoryAdapter);

    @Test
    void shouldRegisterNewUser_whenEmailAndProfileAreValid() {
        RegisterRequest request = new RegisterRequest("João Silva", "joao@email.com", DOCUMENT_NUMBER, "senha123", "ADMINISTRATOR");
        when(userRepositoryAdapter.findByEmail("joao@email.com")).thenReturn(null);
        ProfileModel profile = new ProfileModel(UUID.randomUUID(), "ADMINISTRATOR", "Administrador");
        when(profileRepository.findByName("ADMINISTRATOR")).thenReturn(profile);

        userService.registerNewUser(request);

        verify(userRepositoryAdapter).save(any(User.class));
    }

    @Test
    void shouldThrowIllegalArgument_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("João Silva", "joao@email.com", DOCUMENT_NUMBER, "senha123", "ADMINISTRATOR");
        when(userRepositoryAdapter.findByEmail("joao@email.com")).thenReturn(UserMock.active());

        assertThatThrownBy(() -> userService.registerNewUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void shouldThrowDuplicatedDocument_whenDocumentNumberAlreadyExists() {
        RegisterRequest request = new RegisterRequest("João Silva", "joao@email.com", DOCUMENT_NUMBER, "senha123", "ADMINISTRATOR");
        when(userRepositoryAdapter.findByEmail("joao@email.com")).thenReturn(null);
        when(userRepositoryAdapter.findByDocumentNumber(DOCUMENT_NUMBER)).thenReturn(UserMock.active());

        assertThatThrownBy(() -> userService.registerNewUser(request))
                .isInstanceOf(DuplicatedDocumentException.class)
                .hasMessageContaining("document number");
    }

    @Test
    void shouldNormalizeDocumentNumber_beforeCheckingUniqueness() {
        RegisterRequest request = new RegisterRequest("João Silva", "joao@email.com", "529.982.247-25", "senha123", "ADMINISTRATOR");
        when(userRepositoryAdapter.findByEmail("joao@email.com")).thenReturn(null);
        ProfileModel profile = new ProfileModel(UUID.randomUUID(), "ADMINISTRATOR", "Administrador");
        when(profileRepository.findByName("ADMINISTRATOR")).thenReturn(profile);

        userService.registerNewUser(request);

        verify(userRepositoryAdapter).findByDocumentNumber(DOCUMENT_NUMBER);
    }

    @Test
    void shouldThrowIllegalArgument_whenProfileNotFound() {
        RegisterRequest request = new RegisterRequest("João Silva", "joao@email.com", DOCUMENT_NUMBER, "senha123", "UNKNOWN");
        when(userRepositoryAdapter.findByEmail("joao@email.com")).thenReturn(null);
        when(profileRepository.findByName("UNKNOWN")).thenReturn(null);

        assertThatThrownBy(() -> userService.registerNewUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Profile not found");
    }

    @Test
    void shouldDeactivateUser_whenUserExists() {
        User user = UserMock.active();
        when(userRepositoryAdapter.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.execute(USER_ID);

        verify(userRepositoryAdapter).save(user);
    }

    @Test
    void shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(userRepositoryAdapter.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.execute(USER_ID))
                .isInstanceOf(UserNotFoundException.class);
    }
}

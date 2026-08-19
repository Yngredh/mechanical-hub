package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.mocks.domain.entities.UserMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.UserModelMock;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ProfileJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.models.ProfileModel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRepositoryAdapterTest {

    private final UserJpaRepository jpaRepository = mock(UserJpaRepository.class);
    private final ProfileJpaRepository profileRepository = mock(ProfileJpaRepository.class);

    private final UserRepositoryAdapter adapter = new UserRepositoryAdapter(jpaRepository, profileRepository);

    private ProfileModel buildProfileModel() {
        return new ProfileModel(
                UUID.fromString("00000000-0000-0000-0000-000000000200"),
                "ADMINISTRATOR",
                "Administrador"
        );
    }

    @Test
    void shouldReturnSavedUser_whenSavingUser() {
        when(profileRepository.findByName(any())).thenReturn(buildProfileModel());
        when(jpaRepository.save(any())).thenReturn(UserModelMock.active());

        User result = adapter.save(UserMock.active());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(UserModelMock.USER_ID);
    }

    @Test
    void shouldLookupProfile_whenSavingUser() {
        when(profileRepository.findByName(any())).thenReturn(buildProfileModel());
        when(jpaRepository.save(any())).thenReturn(UserModelMock.active());

        adapter.save(UserMock.active());

        verify(profileRepository).findByName(any());
    }

    @Test
    void shouldReturnUser_whenFindByIdAndUserExists() {
        when(jpaRepository.findByIdAndDeletedAtIsNull(UserModelMock.USER_ID))
                .thenReturn(Optional.of(UserModelMock.active()));

        Optional<User> result = adapter.findById(UserModelMock.USER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(UserModelMock.USER_ID);
    }

    @Test
    void shouldReturnEmpty_whenFindByIdAndUserDoesNotExist() {
        when(jpaRepository.findByIdAndDeletedAtIsNull(UserModelMock.USER_ID))
                .thenReturn(Optional.empty());

        Optional<User> result = adapter.findById(UserModelMock.USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnUser_whenFindByEmailAndUserExists() {
        when(jpaRepository.findByEmail("joao@email.com")).thenReturn(UserModelMock.active());

        User result = adapter.findByEmail("joao@email.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void shouldReturnNull_whenFindByEmailAndUserDoesNotExist() {
        when(jpaRepository.findByEmail("naoexiste@email.com")).thenReturn(null);

        User result = adapter.findByEmail("naoexiste@email.com");

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnUser_whenFindByDocumentNumberAndUserExists() {
        when(jpaRepository.findByDocumentNumber(UserModelMock.DOCUMENT_NUMBER))
                .thenReturn(UserModelMock.active());

        User result = adapter.findByDocumentNumber(UserModelMock.DOCUMENT_NUMBER);

        assertThat(result).isNotNull();
        assertThat(result.getDocumentNumber()).isEqualTo(UserModelMock.DOCUMENT_NUMBER);
    }

    @Test
    void shouldReturnNull_whenFindByDocumentNumberAndUserDoesNotExist() {
        when(jpaRepository.findByDocumentNumber("11144477735")).thenReturn(null);

        User result = adapter.findByDocumentNumber("11144477735");

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnAllUsers_whenFindAll() {
        when(jpaRepository.findByDeletedAtIsNull()).thenReturn(List.of(UserModelMock.active()));

        List<User> result = adapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(UserModelMock.USER_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenDeletingById() {
        adapter.deleteById(UserModelMock.USER_ID);

        verify(jpaRepository).deleteById(UserModelMock.USER_ID);
    }
}

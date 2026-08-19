package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.exceptions.UserAlreadyDeletedException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");
    private static final String DOCUMENT_NUMBER = "52998224725";

    @Test
    void shouldCreateUser_withValidData() {
        Profile profile = new Profile();

        User user = User.create(
                "João Silva",
                "joao@email.com",
                DOCUMENT_NUMBER,
                "hashed_password_123",
                profile
        );

        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo("João Silva");
        assertThat(user.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void shouldBuildUser_withProvidedId() {
        Profile profile = new Profile();

        User user = User.build(
                USER_ID,
                "Maria Silva",
                "maria@email.com",
                DOCUMENT_NUMBER,
                "hashed_password_456",
                profile
        );

        assertThat(user.getId()).isEqualTo(USER_ID);
        assertThat(user.getName()).isEqualTo("Maria Silva");
    }

    @Test
    void shouldDeactivateUser_settingDeletedAt() {
        User user = User.create(
                "João Silva",
                "joao@email.com",
                DOCUMENT_NUMBER,
                "hashed_password",
                new Profile()
        );

        user.deactivate();

        assertThat(user.getDeletedAt()).isNotNull();
        assertThat(user.isDeleted()).isTrue();
    }

    @Test
    void shouldThrowException_whenDeactivatingAlreadyDeletedUser() {
        User user = User.create(
                "João Silva",
                "joao@email.com",
                DOCUMENT_NUMBER,
                "hashed_password",
                new Profile()
        );
        user.deactivate();

        assertThatThrownBy(user::deactivate)
                .isInstanceOf(UserAlreadyDeletedException.class)
                .hasMessageContaining("already deactivated");
    }

    @Test
    void shouldReturnTrue_whenUserIsDeleted() {
        User user = User.create(
                "João Silva",
                "joao@email.com",
                DOCUMENT_NUMBER,
                "hashed_password",
                new Profile()
        );
        user.deactivate();

        assertThat(user.isDeleted()).isTrue();
    }

    @Test
    void shouldReturnFalse_whenUserIsNotDeleted() {
        User user = User.create(
                "João Silva",
                "joao@email.com",
                DOCUMENT_NUMBER,
                "hashed_password",
                new Profile()
        );

        assertThat(user.isDeleted()).isFalse();
    }

    @Test
    void shouldReturnTrue_whenUserIsActive() {
        User user = User.create(
                "João Silva",
                "joao@email.com",
                DOCUMENT_NUMBER,
                "hashed_password",
                new Profile()
        );

        assertThat(user.isActive()).isTrue();
    }

    @Test
    void shouldReturnFalse_whenUserIsInactive() {
        User user = User.create(
                "João Silva",
                "joao@email.com",
                DOCUMENT_NUMBER,
                "hashed_password",
                new Profile()
        );
        user.deactivate();

        assertThat(user.isActive()).isFalse();
    }
}


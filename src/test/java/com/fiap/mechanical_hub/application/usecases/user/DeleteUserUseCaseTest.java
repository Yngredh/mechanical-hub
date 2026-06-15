package com.fiap.mechanical_hub.application.usecases.user;

import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.exceptions.UserNotFoundException;
import com.fiap.mechanical_hub.domain.repositories.UserRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.UserMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteUserUseCaseTest {

    private static final UUID USER_ID = UserMock.USER_ID;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final DeleteUserUseCase useCase = new DeleteUserUseCase(userRepository);

    @Test
    void shouldDeactivateAndSaveUser_whenUserExists() {
        User user = UserMock.active();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        useCase.execute(USER_ID);

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(USER_ID))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldSaveUserAfterDeactivation_whenUserExists() {
        User user = UserMock.active();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        useCase.execute(USER_ID);

        verify(userRepository).save(any(User.class));
    }
}

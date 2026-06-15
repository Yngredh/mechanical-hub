package com.fiap.mechanical_hub.application.usecases.user;

import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.repositories.UserRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.UserMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindAllUsersUseCaseTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final FindAllUsersUseCase useCase = new FindAllUsersUseCase(userRepository);

    @Test
    void shouldReturnAllUsers_whenUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of(UserMock.active()));

        List<User> result = useCase.execute();

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnEmptyList_whenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> result = useCase.execute();

        assertThat(result).isEmpty();
    }
}

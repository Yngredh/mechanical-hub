package com.fiap.mechanical_hub.infrastructure.service;

import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.UserModelMock;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthorizationServiceTest {

    private final UserJpaRepository userRepository = mock(UserJpaRepository.class);
    private final AuthorizationService service = new AuthorizationService(userRepository);

    @Test
    void shouldReturnUserDetails_whenUserExists() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(UserModelMock.active());

        UserDetails result = service.loadUserByUsername("joao@email.com");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("joao@email.com");
    }

    @Test
    void shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(null);

        assertThatThrownBy(() -> service.loadUserByUsername("unknown@email.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("unknown@email.com");
    }
}

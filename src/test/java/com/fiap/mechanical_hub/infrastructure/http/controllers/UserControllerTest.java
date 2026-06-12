package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.authentication.RegisterRequest;
import com.fiap.mechanical_hub.application.dto.user.UserResponse;
import com.fiap.mechanical_hub.application.mappers.UserMapper;
import com.fiap.mechanical_hub.application.usecases.user.DeleteUserUseCase;
import com.fiap.mechanical_hub.application.usecases.user.FindAllUsersUseCase;
import com.fiap.mechanical_hub.infrastructure.service.UserService;
import com.fiap.mechanical_hub.mocks.domain.entities.UserMock;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");

    private final UserMapper mapper = mock(UserMapper.class);
    private final UserService userService = mock(UserService.class);
    private final DeleteUserUseCase deleteUserUseCase = mock(DeleteUserUseCase.class);
    private final FindAllUsersUseCase findAllUsersUseCase = mock(FindAllUsersUseCase.class);

    private final UserController controller = new UserController(
            mapper, userService, deleteUserUseCase, findAllUsersUseCase
    );

    @Test
    void shouldReturnNoContent_whenDeletingUser() {
        ResponseEntity<Void> response = controller.delete(USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(deleteUserUseCase).execute(USER_ID);
    }

    @Test
    void shouldReturnOk_whenListingAllUsers() {
        UserResponse userResponse = new UserResponse(USER_ID, "João Silva", "joao@email.com", "ADMIN");
        when(findAllUsersUseCase.execute()).thenReturn(List.of(UserMock.active()));
        when(mapper.toResponse(any())).thenReturn(userResponse);

        ResponseEntity<List<UserResponse>> response = controller.list();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnOk_whenRegisteringNewUser() {
        RegisterRequest request = new RegisterRequest("João", "joao@email.com", "senha123", "Admin");
        doNothing().when(userService).registerNewUser(request);

        ResponseEntity<String> response = controller.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).registerNewUser(request);
    }

    @Test
    void shouldReturnBadRequest_whenRegisteringUserWithExistingEmail() {
        RegisterRequest request = new RegisterRequest("João", "joao@email.com", "senha123", "Admin");
        doThrow(new IllegalArgumentException("User with this email already exists"))
                .when(userService).registerNewUser(request);

        ResponseEntity<String> response = controller.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("already exists");
    }
}

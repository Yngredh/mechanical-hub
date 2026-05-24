package com.fiap.mechanical_hub.application.usecases.user;

import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.exceptions.UserNotFoundException;
import com.fiap.mechanical_hub.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteUserUseCase {

    private final UserRepository userRepository;

    @Transactional
    public void execute(UUID userId) {
        log.info("Deleting user with id: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        user.deactivate();

        userRepository.save(user);

        log.info("User with id: {} deleted successfully", userId);
    }
}


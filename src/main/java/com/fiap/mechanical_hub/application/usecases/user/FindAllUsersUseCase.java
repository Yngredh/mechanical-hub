package com.fiap.mechanical_hub.application.usecases.user;

import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindAllUsersUseCase {

    private final UserRepository userRepository;

    public List<User> execute() {
        log.info("Finding all users");

        List<User> users = userRepository.findAll();

        log.info("Found {} users", users.size());

        return users;
    }
}

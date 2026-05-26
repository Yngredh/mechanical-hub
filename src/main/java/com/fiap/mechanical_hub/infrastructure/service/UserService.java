package com.fiap.mechanical_hub.infrastructure.service;

import com.fiap.mechanical_hub.application.dto.authentication.RegisterRequest;
import com.fiap.mechanical_hub.domain.entities.Profile;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import com.fiap.mechanical_hub.domain.exceptions.UserNotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.models.ProfileModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ProfileJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.UserRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final ProfileJpaRepository profileRepository;
    private final UserRepositoryAdapter userRepositoryAdapter;

    public void registerNewUser(RegisterRequest request) {
        if (userRepositoryAdapter.findByEmail(request.login()) != null) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        ProfileModel profile = profileRepository.findByName(request.profile());
        if (profile == null) {
            throw new IllegalArgumentException("Profile not found: " + request.profile());
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(request.password());
        User newUser = User.create(
                request.name(),
                request.login(),
                encryptedPassword,
                Profile.create(ProfileEnum.valueOf(request.profile()))
        );

        this.userRepositoryAdapter.save(newUser);
    }

    @Transactional
    public void execute(UUID userId) {
        log.info("Deleting user with id: {}", userId);

        User user = userRepositoryAdapter.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        user.deactivate();

        userRepositoryAdapter.save(user);

        log.info("User with id: {} deleted successfully", userId);
    }

}

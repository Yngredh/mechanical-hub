package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.repositories.UserRepository;
import com.fiap.mechanical_hub.infrastructure.database.mappers.UserRepositoryMapper;
import com.fiap.mechanical_hub.infrastructure.database.models.ProfileModel;
import com.fiap.mechanical_hub.infrastructure.database.models.UserModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ProfileJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final ProfileJpaRepository profileRepository;

    @Override
    public User save(User user) {
        ProfileModel profile = profileRepository.findByName(user.getProfile().getName());
        UserModel userModel = UserRepositoryMapper.toModel(
                user,
                profile
        );

        UserModel saved = jpaRepository.save(userModel);
        return UserRepositoryMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id).map(UserRepositoryMapper::toDomain);
    }

    public User findByEmail(String email) {
        UserModel userModel = jpaRepository.findByEmail(email);
        if (userModel == null) return null;

        return UserRepositoryMapper.toDomain(userModel);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findByDeletedAtIsNull().stream()
                .map(UserRepositoryMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

}


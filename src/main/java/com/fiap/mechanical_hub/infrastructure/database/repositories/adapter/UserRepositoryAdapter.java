package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.repositories.UserRepository;
import com.fiap.mechanical_hub.infrastructure.database.mappers.UserMapper;
import com.fiap.mechanical_hub.infrastructure.database.models.UserModel;
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

    @Override
    public User save(User user) {
        UserModel userModel = new UserModel();
        userModel.setId(user.getId());
        userModel.setName(user.getName());
        userModel.setEmail(user.getEmail());
        userModel.setPasswordHash(user.getPasswordHash());
        userModel.setDeletedAt(user.getDeletedAt());

        UserModel saved = jpaRepository.save(userModel);
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id).map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findByDeletedAtIsNull().stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

}


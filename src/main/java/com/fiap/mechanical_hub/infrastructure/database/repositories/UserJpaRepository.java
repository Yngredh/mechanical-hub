package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.infrastructure.database.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserModel, UUID> {
    UserModel findByEmail(String email);

    UserModel findByDocumentNumber(String documentNumber);

    Optional<UserModel> findByIdAndDeletedAtIsNull(UUID id);

    List<UserModel> findByDeletedAtIsNull();
}

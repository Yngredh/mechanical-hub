package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.infrastructure.database.models.MaterialModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaterialJpaRepository extends JpaRepository<MaterialModel, UUID> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}


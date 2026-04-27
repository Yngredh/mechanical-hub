package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceJpaRepository extends JpaRepository<ServiceModel, UUID> {
    List<ServiceModel> findByIdIn(List<UUID> ids);
}


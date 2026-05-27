package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceMaterialJpaRepository extends JpaRepository<ServiceMaterialModel, UUID> {
    List<ServiceMaterialModel> findByServiceId(UUID serviceId);

    List<ServiceMaterialModel> findByMaterialId(UUID materialId);

}


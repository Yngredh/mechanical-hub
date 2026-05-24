package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceJpaRepository extends JpaRepository<ServiceModel, UUID> {
    Optional<ServiceModel> findByIdAndDeletedAtIsNull(UUID id);

    List<ServiceModel> findByDeletedAtIsNull();

    @Query("SELECT s FROM ServiceModel s WHERE s.id IN :serviceIds")
    List<ServiceModel> findAllIn(List<UUID> serviceIds);

    @Modifying
    @Query("UPDATE ServiceModel s SET s.deletedAt = CURRENT_TIMESTAMP WHERE s.id = :serviceId")
    void softDelete(@Param("serviceId") UUID serviceId);
}


package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.infrastructure.database.models.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerModel, UUID> {

    Optional<CustomerModel> findByDocumentNumber(String documentNumber);

    Optional<CustomerModel> findByIdAndDeletedAtIsNull(UUID id);

    List<CustomerModel> findByDeletedAtIsNull();

    boolean existsByDocumentNumber(String documentNumber);

}


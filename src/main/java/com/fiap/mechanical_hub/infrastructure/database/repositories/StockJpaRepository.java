package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.StockModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockJpaRepository extends JpaRepository<StockModel, UUID> {

    Optional<StockModel> findByMaterialId(UUID materialId);

    Optional<StockModel> findByMaterialIdAndStatus(UUID materialId, StockStatusEnum status);

    List<StockModel> findAllByMaterialId(UUID materialId);
}


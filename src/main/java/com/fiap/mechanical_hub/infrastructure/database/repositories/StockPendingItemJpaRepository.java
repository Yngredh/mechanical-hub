package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.infrastructure.database.models.StockPendingItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockPendingItemJpaRepository extends JpaRepository<StockPendingItemModel, UUID> {

    List<StockPendingItemModel> findByMaterialIdOrderByCreatedAtAsc(UUID materialId);

}


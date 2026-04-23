package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.infrastructure.database.models.StockMovementModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementJpaRepository extends JpaRepository<StockMovementModel, UUID> {

    @Query(value = "SELECT * FROM stock_movements WHERE material_id = ?1 ORDER BY created_at DESC", nativeQuery = true)
    List<StockMovementModel> findByMaterialIdOrderByCreatedAtDesc(UUID materialId);
}


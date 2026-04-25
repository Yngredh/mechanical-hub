package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceOrderJpaRepository extends JpaRepository<ServiceOrderModel, UUID> {

    @Query(value = "SELECT order_number FROM service_orders WHERE order_number LIKE CONCAT('OS-', :yearMonth, '-%') ORDER BY order_number DESC LIMIT 1", nativeQuery = true)
    Optional<String> findLastOrderNumberByYearMonth(@Param("yearMonth") String yearMonth);
}

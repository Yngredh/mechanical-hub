package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceOrderJpaRepository extends JpaRepository<ServiceOrderModel, UUID>, JpaSpecificationExecutor<ServiceOrderModel> {

    List<ServiceOrderModel> findAllByOrderByCreatedAtDesc();

    Optional<ServiceOrderModel> findByOrderNumber(String orderNumber);

    @Query(value = "SELECT order_number FROM service_orders WHERE order_number LIKE CONCAT('OS-', :yearMonth, '-%') ORDER BY order_number DESC LIMIT 1", nativeQuery = true)
    Optional<String> findLastOrderNumberByYearMonth(@Param("yearMonth") String yearMonth);

    @Query("""
                SELECT so
                FROM ServiceOrderModel so
                WHERE so.customerId = :customerId
                ORDER BY so.createdAt DESC
           \s""")
    List<ServiceOrderModel> findSummaryByCustomerId(@Param("customerId") UUID customerId);

    @Query(value = """
            SELECT 
                so.id,
                so.order_number,
                so.order_status,
                c.name,
                CONCAT(v.brand, ' ', v.model, ' (', v.license_plate, ')'),
                so.budget,
                so.created_at
            FROM service_orders so
            JOIN customers c ON so.customer_id = c.id
            JOIN vehicles v ON so.vehicle_id = v.id
            WHERE (:status IS NULL OR so.order_status = :status)
              AND (:customerId IS NULL OR so.customer_id = :customerId)
              AND (:startDate IS NULL OR so.created_at >= :startDate)
              AND (:endDate IS NULL OR so.created_at <= :endDate)
            ORDER BY so.created_at DESC
            """, nativeQuery = true)
    List<ServiceOrderSummaryResponse> findAllSummaries(
            @Param("status") String status,
            @Param("customerId") UUID customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
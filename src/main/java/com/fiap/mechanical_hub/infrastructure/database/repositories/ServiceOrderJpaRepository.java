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
import java.util.UUID;

@Repository
public interface ServiceOrderJpaRepository extends JpaRepository<ServiceOrderModel, UUID>, JpaSpecificationExecutor<ServiceOrderModel> {

    @Query("""
                SELECT new com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse(
                    so.id,
                    so.orderNumber,
                    so.orderStatus,\s
                    c.name,
                    CONCAT(v.brand, ' ', v.model, ' (', v.licensePlate, ')'),
                    so.budget,
                    so.createdAt
                )
                FROM ServiceOrderModel so
                JOIN CustomerModel c ON so.customerId = c.id
                JOIN VehicleModel v ON so.vehicleId = v.id
                WHERE so.customerId = :customerId
                ORDER BY so.createdAt DESC
           \s""")
    List<ServiceOrder> findSummaryByCustomerId(@Param("customerId") UUID customerId);

    @Query("""
                SELECT new com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse(
                    so.id,
                    so.orderNumber,
                    so.orderStatus,\s
                    c.name,
                    CONCAT(v.brand, ' ', v.model, ' (', v.licensePlate, ')'),
                    so.budget,
                    so.createdAt
                )
                FROM ServiceOrderModel so
                JOIN CustomerModel c ON so.customerId = c.id
                JOIN VehicleModel v ON so.vehicleId = v.id
                WHERE (:status IS NULL OR so.orderStatus = :status)
                  AND (:customerId IS NULL OR so.customerId = :customerId)
                  AND (:startDate IS NULL OR so.createdAt >= :startDate)
                  AND (:endDate IS NULL OR so.createdAt <= :endDate)
           \s""")
    List<ServiceOrderSummaryResponse> findAllSummaries(
            @Param("status") String status,
            @Param("customerId") UUID customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}

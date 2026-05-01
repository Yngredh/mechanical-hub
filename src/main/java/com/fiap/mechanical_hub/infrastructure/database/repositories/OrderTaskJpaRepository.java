package com.fiap.mechanical_hub.infrastructure.database.repositories;

import com.fiap.mechanical_hub.infrastructure.database.models.OrderTaskModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderTaskJpaRepository extends JpaRepository<OrderTaskModel, UUID> {

    List<OrderTaskModel> findByServiceOrderId(UUID serviceOrderId);

    @Query(nativeQuery = true, value = """
            SELECT\s
                ot.service_id as serviceId,
                s.name as serviceName,
                CAST(AVG(EXTRACT(EPOCH FROM (ot.finished_at - ot.started_at)) / 60) AS BIGINT) as avgExecutionMinutes,
                COUNT(ot.id) as totalExecutions
            FROM order_tasks ot
            JOIN serviceData s ON ot.service_id = s.id
            WHERE ot.started_at IS NOT NULL\s
                AND ot.finished_at IS NOT NULL
            GROUP BY ot.service_id, s.name
            ORDER BY s.name ASC
           \s""")
    List<Object[]> findAverageExecutionTimeByService();
}

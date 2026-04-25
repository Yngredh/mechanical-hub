package com.fiap.mechanical_hub.infrastructure.database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_tasks")
@Getter
@Setter
public class OrderTaskModel {

    @Id
    private UUID id;

    @Column(name = "service_order_id", nullable = false)
    private UUID serviceOrderId;

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @Column(name = "service_status", nullable = false)
    private String serviceStatus;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public OrderTaskModel() {
    }

    public OrderTaskModel(UUID id, UUID serviceOrderId, UUID serviceId, String serviceStatus, 
                          LocalDateTime startedAt, LocalDateTime finishedAt) {
        this.id = id;
        this.serviceOrderId = serviceOrderId;
        this.serviceId = serviceId;
        this.serviceStatus = serviceStatus;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }
}


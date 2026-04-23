package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderTask {

    private UUID id;
    private UUID serviceOrderId;
    private UUID serviceId;
    private TaskStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public static OrderTask create(UUID serviceOrderId, UUID serviceId) {
        OrderTask task = new OrderTask();
        task.id = UUID.randomUUID();
        task.serviceOrderId = serviceOrderId;
        task.serviceId = serviceId;
        task.status = TaskStatus.PENDENTE;
        task.startedAt = null;
        task.finishedAt = null;

        return task;
    }

    public void start() {
        if (status != TaskStatus.PENDENTE) {
            throw new IllegalStateException("Task must be in PENDENTE status to start");
        }
        this.status = TaskStatus.INICIADO;
        this.startedAt = LocalDateTime.now();
    }

    public void finish() {
        if (status != TaskStatus.INICIADO) {
            throw new IllegalStateException("Task must be in INICIADO status to finish");
        }
        this.status = TaskStatus.FINALIZADO;
        this.finishedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status != TaskStatus.INICIADO) {
            throw new IllegalStateException("Task must be in INICIADO status to cancel");
        }
        this.status = TaskStatus.CANCELADO;
        this.finishedAt = LocalDateTime.now();
    }

    public boolean isFinished() {
        return status == TaskStatus.FINALIZADO;
    }
}


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
            throw new IllegalStateException("Tarefa precisa estar em status PENDENTE para ser iniciada");
        }
        this.status = TaskStatus.INICIADO;
        this.startedAt = LocalDateTime.now();
    }

    public void approve() {
        if (status != TaskStatus.INICIADO) {
            throw new IllegalStateException("Tarefa precisa estar em status INICIADO para ser aprovada");
        }
        this.status = TaskStatus.APROVADO;
        this.startedAt = LocalDateTime.now();
    }

    public void refuse() {
        if (status != TaskStatus.INICIADO) {
            throw new IllegalStateException("Tarefa precisa estar em status INICIADO para ser recusada");
        }
        this.status = TaskStatus.RECUSADO;
        this.startedAt = LocalDateTime.now();
    }

    public void finish() {
        if (status != TaskStatus.INICIADO) {
            throw new IllegalStateException("Task must be in INICIADO status to finish");
        }
        this.status = TaskStatus.FINALIZADO;
        this.finishedAt = LocalDateTime.now();
    }

    public boolean isFinished() {
        return status == TaskStatus.FINALIZADO;
    }
}


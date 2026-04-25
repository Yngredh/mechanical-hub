package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
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
    private TaskStatusEnum status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public static OrderTask create(UUID serviceOrderId, UUID serviceId) {
        OrderTask task = new OrderTask();
        task.id = UUID.randomUUID();
        task.serviceOrderId = serviceOrderId;
        task.serviceId = serviceId;
        task.status = TaskStatusEnum.PENDENTE;
        task.startedAt = null;
        task.finishedAt = null;

        return task;
    }

    public void start() {
        if (status != TaskStatusEnum.PENDENTE) {
            throw new IllegalStateException("Tarefa precisa estar em status PENDENTE para ser iniciada");
        }
        this.status = TaskStatusEnum.INICIADO;
        this.startedAt = LocalDateTime.now();
    }

    public void approve() {
        if (status != TaskStatusEnum.INICIADO) {
            throw new IllegalStateException("Tarefa precisa estar em status INICIADO para ser aprovada");
        }
        this.status = TaskStatusEnum.APROVADO;
        this.startedAt = LocalDateTime.now();
    }

    public void refuse() {
        if (status != TaskStatusEnum.INICIADO) {
            throw new IllegalStateException("Tarefa precisa estar em status INICIADO para ser recusada");
        }
        this.status = TaskStatusEnum.RECUSADO;
        this.startedAt = LocalDateTime.now();
    }

    public void finish() {
        if (status != TaskStatusEnum.INICIADO) {
            throw new IllegalStateException("Task must be in INICIADO status to finish");
        }
        this.status = TaskStatusEnum.FINALIZADO;
        this.finishedAt = LocalDateTime.now();
    }

    public boolean isFinished() {
        return status == TaskStatusEnum.FINALIZADO;
    }
}


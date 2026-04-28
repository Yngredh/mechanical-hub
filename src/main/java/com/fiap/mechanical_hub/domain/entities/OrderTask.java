package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
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
    private Service service;
    private TaskStatusEnum status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public static OrderTask create(UUID serviceOrderId, Service service) {
        OrderTask task = new OrderTask();
        task.id = UUID.randomUUID();
        task.serviceOrderId = serviceOrderId;
        task.service = service;
        task.status = TaskStatusEnum.PENDENTE;
        task.startedAt = null;
        task.finishedAt = null;

        return task;
    }

    public void start() {
        if (status != TaskStatusEnum.PENDENTE) {
            throw new BusinessRuleException("Para iniciar uma tarefa, esta precisa estar em PENDENTE");
        }

        this.status = TaskStatusEnum.INICIADO;
        this.startedAt = LocalDateTime.now();
    }

    public void finish() {
        if (status != TaskStatusEnum.INICIADO) {
            throw new BusinessRuleException("Para finalizar uma tarefa, esta precisa em INICIADO");
        }

        this.status = TaskStatusEnum.FINALIZADO;
        this.finishedAt = LocalDateTime.now();
    }

    public boolean isFinished() {
        return status == TaskStatusEnum.FINALIZADO;
    }

}


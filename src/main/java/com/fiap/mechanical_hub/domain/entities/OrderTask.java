package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTask {

    private UUID id;
    private UUID serviceOrderId;
    private ServiceData serviceData;
    private TaskStatusEnum status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public static OrderTask create(UUID serviceOrderId, ServiceData serviceData) {
        OrderTask task = new OrderTask();
        task.id = UUID.randomUUID();
        task.serviceOrderId = serviceOrderId;
        task.serviceData = serviceData;
        task.status = TaskStatusEnum.PENDENTE;
        task.startedAt = null;
        task.finishedAt = null;

        return task;
    }

    public void start() {
        if (status != TaskStatusEnum.PENDENTE) {
            throw new BusinessRuleException("Transição de status não permitida");
        }

        this.status = TaskStatusEnum.INICIADO;
        this.startedAt = LocalDateTime.now();
    }

    public void finish() {
        if (status != TaskStatusEnum.INICIADO) {
            throw new BusinessRuleException("Transição de status não permitida");
        }

        this.status = TaskStatusEnum.FINALIZADO;
        this.finishedAt = LocalDateTime.now();
    }

    public boolean isFinished() {
        return status == TaskStatusEnum.FINALIZADO;
    }

}


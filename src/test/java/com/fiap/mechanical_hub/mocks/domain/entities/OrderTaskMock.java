package com.fiap.mechanical_hub.mocks.domain.entities;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderTaskMock {

    private static final UUID TASK_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final UUID SERVICE_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static OrderTask notStarted() {
        return OrderTask.builder()
                .id(TASK_ID)
                .serviceOrderId(SERVICE_ORDER_ID)
                .serviceData(ServiceDataMock.withDefaultValues())
                .status(TaskStatusEnum.PENDENTE)
                .startedAt(null)
                .finishedAt(null)
                .build();
    }

    public static OrderTask started() {
        LocalDateTime startTime = LocalDateTime.now();
        return OrderTask.builder()
                .id(TASK_ID)
                .serviceOrderId(SERVICE_ORDER_ID)
                .serviceData(ServiceDataMock.withDefaultValues())
                .status(TaskStatusEnum.INICIADO)
                .startedAt(startTime)
                .finishedAt(null)
                .build();
    }

    public static OrderTask finished() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime finishTime = startTime.plusHours(2);
        return OrderTask.builder()
                .id(TASK_ID)
                .serviceOrderId(SERVICE_ORDER_ID)
                .serviceData(ServiceDataMock.withDefaultValues())
                .status(TaskStatusEnum.FINALIZADO)
                .startedAt(startTime)
                .finishedAt(finishTime)
                .build();
    }

    public static OrderTask withRecordedTimes(LocalDateTime startedAt, LocalDateTime finishedAt) {
        return OrderTask.builder()
                .id(TASK_ID)
                .serviceOrderId(SERVICE_ORDER_ID)
                .serviceData(ServiceDataMock.withDefaultValues())
                .status(TaskStatusEnum.FINALIZADO)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .build();
    }
}


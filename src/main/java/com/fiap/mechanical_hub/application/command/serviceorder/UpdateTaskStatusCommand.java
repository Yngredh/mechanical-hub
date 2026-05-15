package com.fiap.mechanical_hub.application.command.serviceorder;

import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import java.util.UUID;

public record UpdateTaskStatusCommand(
    UUID orderId,
    UUID taskId,
    TaskStatusEnum status
) { }


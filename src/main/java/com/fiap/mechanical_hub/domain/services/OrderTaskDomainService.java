package com.fiap.mechanical_hub.domain.services;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatus;
import com.fiap.mechanical_hub.domain.enums.TaskStatus;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderStatusTransitionException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.OrderTaskRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
public class OrderTaskDomainService {

    private final OrderTaskRepository orderTaskRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    public OrderTask updateStatus(UUID orderTaskId, String newStatusString) {
        OrderTask task = orderTaskRepository.findById(orderTaskId)
                .orElseThrow(() -> new NotFoundException("Order task with id " + orderTaskId + " not found"));

        TaskStatus newStatus = TaskStatus.fromString(newStatusString);

        validateStatusTransition(task.getStatus(), newStatus);

        switch (newStatus) {
            case INICIADO:
                task.start();
                updateServiceOrderToEmExecucao(task.getServiceOrderId());
                break;
            case FINALIZADO:
                task.finish();
                break;
            default:
                throw new InvalidOrderStatusTransitionException(
                    task.getStatus().getDisplayName(),
                    newStatus.getDisplayName()
                );
        }

        return orderTaskRepository.save(task);
    }

    private void validateStatusTransition(TaskStatus currentStatus, TaskStatus newStatus) {
        if (currentStatus == TaskStatus.PENDENTE && newStatus == TaskStatus.INICIADO) {
            return; // Valid: PENDENTE → INICIADO
        }
        if (currentStatus == TaskStatus.INICIADO && newStatus == TaskStatus.FINALIZADO) {
            return; // Valid: INICIADO → FINALIZADO
        }

        throw new InvalidOrderStatusTransitionException(
            currentStatus.getDisplayName(),
            newStatus.getDisplayName()
        );
    }

    private void updateServiceOrderToEmExecucao(UUID serviceOrderId) {
        ServiceOrder order = serviceOrderRepository.findById(serviceOrderId)
                .orElseThrow(() -> new NotFoundException("Service order with id " + serviceOrderId + " not found"));

        if (order.getStatus() == OrderStatus.APROVADO ||
            order.getStatus() == OrderStatus.EM_DIAGNOSTICO) {
            order.startExecution();
            serviceOrderRepository.save(order);
        }
    }
}

package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.UpdateTaskStatusCommand;
import com.fiap.mechanical_hub.application.usecases.StockUseCase;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateTaskStatusUseCase {

    private final ServiceOrderRepository repository;
    private final StockUseCase stockUseCase;

    @Transactional
    public void execute(UpdateTaskStatusCommand command) {
        log.info("Updating task {} status to {}", command.taskId(), command.status());

        ServiceOrder order = repository.findById(command.orderId())
                .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada"));

        switch (command.status()) {
            case INICIADO -> {
                order.startTask(command.taskId());
                log.info("Task {} started", command.taskId());
            }
            case FINALIZADO -> {
                order.finishTask(command.taskId());
                OrderTask task = order.getOrderTasks().stream()
                        .filter(t -> t.getServiceData().getId().equals(command.taskId()))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Tarefa não encontrada"));
                stockUseCase.registerStockOut(order, task);
                log.info("Task {} finished", command.taskId());
            }
            default -> throw new IllegalArgumentException("Status não reconhecido para atualização: " + command.status());
        }

        repository.save(order);
    }
}



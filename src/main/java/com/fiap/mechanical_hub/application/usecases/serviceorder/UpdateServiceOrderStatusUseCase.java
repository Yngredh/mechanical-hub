package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.UpdateServiceOrderStatusCommand;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransitionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateServiceOrderStatusUseCase {

    private final ServiceOrderRepository repository;
    private final OrderStatusTransitionFactory factory;

    @Transactional
    public ServiceOrder execute(UpdateServiceOrderStatusCommand command) {
        log.info("Updating service order {} status to {}", command.orderId(), command.targetStatus());

        ServiceOrder order = repository.findById(command.orderId())
                .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada com ID: " + command.orderId()));

        factory.get(command.targetStatus()).execute(order);
        ServiceOrder updatedOrder = repository.save(order);

        log.info("Service order {} status updated successfully", command.orderId());
        return updatedOrder;
    }
}


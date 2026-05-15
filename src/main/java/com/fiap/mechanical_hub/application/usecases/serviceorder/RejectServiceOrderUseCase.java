package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.RejectServiceOrderCommand;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
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
public class RejectServiceOrderUseCase {

    private final ServiceOrderRepository repository;
    private final OrderStatusTransitionFactory factory;

    @Transactional
    public void execute(RejectServiceOrderCommand command) {
        log.info("Rejecting service order: {}", command.serviceOrderId());

        ServiceOrder order = repository.findById(command.serviceOrderId())
                .orElseThrow(() -> new NotFoundException("Service order with id " + command.serviceOrderId() + " not found"));

        factory.get(OrderStatusEnum.RECUSADO).execute(order);
        repository.save(order);

        log.info("Service order {} rejected successfully", command.serviceOrderId());
    }
}


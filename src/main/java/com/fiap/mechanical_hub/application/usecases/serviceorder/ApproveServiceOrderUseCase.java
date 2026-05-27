package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.ApproveServiceOrderCommand;
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
public class ApproveServiceOrderUseCase {

    private final ServiceOrderRepository repository;
    private final OrderStatusTransitionFactory factory;

    @Transactional
    public void execute(ApproveServiceOrderCommand command) {
        log.info("Approving service order: {}", command.serviceOrderId());

        ServiceOrder order = repository.findById(command.serviceOrderId())
                .orElseThrow(() -> new NotFoundException("Service order with id " + command.serviceOrderId() + " not found"));

        factory.get(OrderStatusEnum.APROVADO).execute(order);
        repository.save(order);

        log.info("Service order {} approved successfully", command.serviceOrderId());
    }
}


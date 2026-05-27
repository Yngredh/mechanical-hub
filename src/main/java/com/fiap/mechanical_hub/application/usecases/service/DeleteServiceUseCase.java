package com.fiap.mechanical_hub.application.usecases.service;

import com.fiap.mechanical_hub.application.command.ordertask.DeleteOrderTaskCommand;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.ServiceNotFoundException;
import com.fiap.mechanical_hub.domain.repositories.OrderTaskRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.domain.service.ServiceOrderDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteServiceUseCase {

    private final ServiceRepository serviceRepository;
    private final OrderTaskRepository orderTaskRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final ServiceOrderDomainService orderDomainService;

    @Transactional
    public void execute(DeleteOrderTaskCommand command) {
        UUID serviceId = command.id();
        log.info("Deleting service with id: {}", serviceId);

        ServiceData service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new ServiceNotFoundException(serviceId.toString()));

        List<OrderTask> tasks = orderTaskRepository.findAllByServiceId(serviceId);

        List<UUID> orderIds = tasks.stream().map(OrderTask::getServiceOrderId).toList();

        List<ServiceOrder> serviceOrders = serviceOrderRepository.findAllIn(orderIds);

        orderDomainService.hasAnyOpenServiceOrder(serviceOrders);

        service.deactivate();

        serviceRepository.deleteById(service.getId());

        log.info("Service deleted with id: {}", serviceId);
    }

}


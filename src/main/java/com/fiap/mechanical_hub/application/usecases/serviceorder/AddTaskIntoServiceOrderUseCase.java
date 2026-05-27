package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.AddTaskIntoServiceOrderCommand;
import com.fiap.mechanical_hub.application.command.stock.ReserveStockForServiceOrderCommand;
import com.fiap.mechanical_hub.application.usecases.service.GetServiceByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.stock.ReserveStockForServiceOrderUseCase;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceMaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddTaskIntoServiceOrderUseCase {

    private final GetServiceByIdUseCase getServiceByIdUseCase;
    private final ReserveStockForServiceOrderUseCase reserveStockForServiceOrderUseCase;

    private final ServiceOrderRepository repository;
    private final ServiceMaterialRepository serviceMaterialRepository;

    @Transactional
    public void execute(AddTaskIntoServiceOrderCommand command) {
        log.info("Adding {} services to service order {}", command.serviceIds().size(), command.serviceOrderId());

        ServiceOrder order = repository.findById(command.serviceOrderId())
                .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada com ID: " + command.serviceOrderId()));

        order.isAddingServiceAvailable();

        boolean hasStockPending = false;
        BigDecimal totalBudget = order.getBudget() != null ? order.getBudget() : BigDecimal.ZERO;

        for (UUID serviceId : command.serviceIds()) {
            if (order.validateTaskNotDuplicated(serviceId)) break;

            ServiceData serviceData = getServiceByIdUseCase.execute(serviceId);

            log.info("Processing service {} for order {}", serviceId, command.serviceOrderId());

            List<ServiceMaterial> serviceMaterials = serviceMaterialRepository.findByServiceId(serviceId);

            for (ServiceMaterial sm : serviceMaterials) {
                UUID materialId = sm.getMaterial().getId();
                int quantity = sm.getQuantity();

                log.info("Reserving {} units of material {} for service {}", quantity, materialId, serviceId);

                var reserveCommand = new ReserveStockForServiceOrderCommand(
                    command.serviceOrderId(),
                    materialId,
                    quantity
                );
                var result = reserveStockForServiceOrderUseCase.execute(reserveCommand);
                if (result == null) hasStockPending = true;
            }

            totalBudget = totalBudget.add(serviceData.getTotalPrice());
            order.addTask(OrderTask.create(order.getId(), serviceData));
        }

        order.updateBudget(totalBudget);
        order.setHasStockPending(hasStockPending);
        repository.save(order);

        log.info("Successfully added services to service order {}. Has pending items: {}", command.serviceOrderId(), hasStockPending);
    }
}


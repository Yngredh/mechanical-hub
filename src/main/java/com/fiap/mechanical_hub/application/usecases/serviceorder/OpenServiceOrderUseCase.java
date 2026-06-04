package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.AddTaskIntoServiceOrderCommand;
import com.fiap.mechanical_hub.application.command.serviceorder.OpenServiceOrderCommand;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.domain.utils.OrderNumberGenerator;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenServiceOrderUseCase {

    private final ServiceOrderRepository serviceOrderRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final AddTaskIntoServiceOrderUseCase addTaskIntoServiceOrderUseCase;
    private final OrderNumberGenerator orderNumberGenerator;
    private final ServiceOrderMapper mapper;

    @Transactional
    public ServiceOrderResponse execute(OpenServiceOrderCommand command) {
        log.info("Opening service order for customer: {} | vehicle: {}", command.customerId(), command.vehicleId());

        var customer = customerRepository.findById(command.customerId())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado com ID: " + command.customerId()));

        var vehicle = vehicleRepository.findById(command.vehicleId())
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado com ID: " + command.vehicleId()));

        if (!vehicle.getCustomerId().equals(command.customerId())) {
            log.warn("Vehicle {} does not belong to customer {}", command.vehicleId(), command.customerId());
            throw new BusinessRuleException("Veículo não pertence ao cliente fornecido");
        }

        String orderNumber = orderNumberGenerator.generate();
        ServiceOrder serviceOrder = ServiceOrder.create(
                vehicle.getId(),
                customer.getId(),
                orderNumber,
                command.requestDescription(),
                command.createdByUserId()
        );

        log.info("Transitioning order {} to EM_DIAGNOSTICO status", serviceOrder.getId());
        serviceOrder.startDiagnosis();

        ServiceOrder savedOrder = serviceOrderRepository.save(serviceOrder);
        log.info("Service order created with number: {} | id: {}", orderNumber, savedOrder.getId());

        if (command.serviceIds() != null && !command.serviceIds().isEmpty()) {
            log.info("Adding {} services to service order {}", command.serviceIds().size(), savedOrder.getId());
            var addServicesCommand = new AddTaskIntoServiceOrderCommand(savedOrder.getId(), command.serviceIds());
            addTaskIntoServiceOrderUseCase.execute(addServicesCommand);

            serviceOrder = serviceOrderRepository.findById(savedOrder.getId())
                    .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada após adição de serviços"));
        }

        log.info("Service order {} opened successfully", savedOrder.getId());
        return mapper.toResponse(serviceOrder);
    }
}



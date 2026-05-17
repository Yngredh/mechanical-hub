package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.CreateServiceOrderCommand;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.application.usecases.vehicle.FindOrCreateVehicleUseCase;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.utils.OrderNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateServiceOrderUseCase {

    private final ServiceOrderRepository repository;
    private final FindOrCreateVehicleUseCase findOrCreateVehicleUseCase;
    private final FindOrCreateServiceOrderCustomerUseCase findOrCreateCustomerUseCase;
    private final OrderNumberGenerator orderNumberGenerator;
    private final ServiceOrderMapper mapper;

    @Transactional
    public ServiceOrderResponse execute(CreateServiceOrderCommand command) {
        log.info("Creating new service order for customer: {}", command.customerName());

        Customer customer = findOrCreateCustomerUseCase.execute(
            command.customerName(),
            command.documentType(),
            command.documentNumber(),
            command.telephone(),
            command.email(),
            command.address()
        );

        Vehicle vehicle = findOrCreateVehicleUseCase.execute(
            customer.getId(),
            command.licensePlate(),
            command.vehicleBrand(),
            command.vehicleModel(),
            command.vehicleYear(),
            command.vehicleColor()
        );

        String orderNumber = orderNumberGenerator.generate();
        ServiceOrder serviceOrder = ServiceOrder.create(
            vehicle.getId(),
            customer.getId(),
            orderNumber,
            command.requestDescription(),
            command.createdByUserId()
        );

        ServiceOrder saved = repository.save(serviceOrder);
        log.info("Service order created successfully with number: {}", orderNumber);

        return mapper.toResponse(saved);
    }
}



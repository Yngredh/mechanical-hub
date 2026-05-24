package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.application.usecases.vehicle.DeleteVehicleUseCase;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.CustomerNotFoundException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
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
public class DeleteCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final DeleteVehicleUseCase deleteVehicleUseCase;
    private final ServiceOrderDomainService orderDomainService;

    @Transactional
    public void execute(UUID customerId) {
        log.info("Deleting customer with id: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId.toString()));

        List<ServiceOrder> customerOrders = customerRepository.findOrdersByCustomerId(customerId);

        orderDomainService.hasAnyOpenServiceOrder(customerOrders);

        deleteVehicleUseCase.execute(customerId);

        customer.deactivate();

        customerRepository.save(customer);

        log.info("Customer with id: {} deleted successfully", customerId);
    }
}


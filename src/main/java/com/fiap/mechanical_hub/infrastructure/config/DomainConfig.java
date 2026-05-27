package com.fiap.mechanical_hub.infrastructure.config;

import com.fiap.mechanical_hub.domain.interfaces.SendBudgetApproval;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.domain.service.CustomerDomainService;
import com.fiap.mechanical_hub.domain.service.ServiceOrderDomainService;
import com.fiap.mechanical_hub.domain.service.VehicleDomainService;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransitionFactory;
import com.fiap.mechanical_hub.domain.strategies.order_transition.TransitionConfig;
import com.fiap.mechanical_hub.domain.utils.OrderNumberGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public CustomerDomainService customerDomainService(CustomerRepository repository) {
        return new CustomerDomainService(repository);
    }

    @Bean
    public VehicleDomainService vehicleDomainService(VehicleRepository repository) {
        return new VehicleDomainService(repository);
    }

    @Bean
    public ServiceOrderDomainService serviceDomainService() {
        return new ServiceOrderDomainService();
    }

    @Bean
    public OrderNumberGenerator orderNumberGenerator(ServiceOrderRepository serviceOrderRepository) {
        return new OrderNumberGenerator(serviceOrderRepository);
    }

    @Bean
    public OrderStatusTransitionFactory orderStatusTransitionFactory(
            SendBudgetApproval sendBudgetApproval
    ) {
        return new TransitionConfig().transitionFactory(sendBudgetApproval);
    }
}

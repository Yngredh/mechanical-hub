package com.fiap.mechanical_hub.infrastructure.config;

import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.domain.service.CustomerDomainService;
import com.fiap.mechanical_hub.domain.service.VehicleDomainService;
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
}

package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.fiap.mechanical_hub.infrastructure.http.mappers.CustomerHttpMapper.toResponse;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindCustomerByIdUseCase {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public CustomerResponse execute(UUID id) {
        log.info("Retrieving customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado para o id: " + id));

        return toResponse(customer);
    }

}


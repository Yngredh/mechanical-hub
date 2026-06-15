package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.infrastructure.database.mappers.ServiceOrderRepositoryMapper;
import com.fiap.mechanical_hub.infrastructure.database.models.CustomerModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.CustomerJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceOrderJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.mappers.CustomerRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.mechanical_hub.infrastructure.database.mappers.CustomerRepositoryMapper.toDomainEntity;
import static com.fiap.mechanical_hub.infrastructure.database.mappers.CustomerRepositoryMapper.toJpaEntity;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;
    private final ServiceOrderJpaRepository serviceOrderJpaRepository;

    @Override
    public Customer save(Customer customer) {
        CustomerModel entity = toJpaEntity(customer);
        CustomerModel saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id).map(CustomerRepositoryMapper::toDomainEntity);
    }

    @Override
    public Optional<Customer> findByDocumentNumber(String documentNumber) {
        return jpaRepository.findByDocumentNumber(documentNumber).map(CustomerRepositoryMapper::toDomainEntity);
    }

    @Override
    public List<Customer> findAll() {
        return jpaRepository.findByDeletedAtIsNull().stream()
                .map(CustomerRepositoryMapper::toDomainEntity)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return jpaRepository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public List<ServiceOrder> findOrdersByCustomerId(UUID customerId) {
        return serviceOrderJpaRepository.findAllOpenOrdersByCustomerId(customerId)
                .stream()
                .map(ServiceOrderRepositoryMapper::toDomainEntity)
                .toList();
    }

}


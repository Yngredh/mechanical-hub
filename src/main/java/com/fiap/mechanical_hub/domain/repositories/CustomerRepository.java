package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByDocumentNumber(String documentNumber);

    List<Customer> findAll();

    void deleteById(UUID id);

    boolean existsByDocumentNumber(String documentNumber);
}


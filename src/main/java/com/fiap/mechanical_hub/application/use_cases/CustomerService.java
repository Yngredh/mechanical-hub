package com.fiap.mechanical_hub.application.use_cases;

import com.fiap.mechanical_hub.application.dto.customer.UpsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.mappers.CustomerMapper;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentType;
import com.fiap.mechanical_hub.domain.exceptions.DuplicateDocumentException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerResponse create(UpsertCustomerRequest request) {
        if (customerRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new DuplicateDocumentException(
                    String.format("Customer with document %s already exists", request.getDocumentNumber())
            );
        }

        Customer customer = customerMapper.toDomainEntity(request);
        Customer savedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + id));
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    public CustomerResponse update(UUID id, UpsertCustomerRequest request) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + id));

        boolean documentNumberMatches = !existingCustomer.getDocumentNumber().equals(request.getDocumentNumber());
        boolean customerExists = customerRepository.existsByDocumentNumber(request.getDocumentNumber());

        if (customerExists && documentNumberMatches) {
            throw new DuplicateDocumentException(
                    String.format("Customer with document %s already exists", request.getDocumentNumber())
            );
        }

        Customer updatedCustomer = customerMapper.toDomainEntity(request, existingCustomer);
        Customer savedCustomer = customerRepository.save(updatedCustomer);

        return customerMapper.toResponse(savedCustomer);
    }

    public void delete(UUID id) {
        if (customerRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    public Customer findByDocumentOrCreate(String name, String documentType, String documentNumber,
                                           String telephone, String email, String address) {
        Optional<Customer> existingCustomer = customerRepository.findByDocumentNumber(documentNumber);
        if (existingCustomer.isPresent()) { return existingCustomer.get(); }

        DocumentType type = DocumentType.fromValue(documentType);
        Customer newCustomer = Customer.create(name, type, documentNumber, telephone, email, address);
        return customerRepository.save(newCustomer);
    }
}


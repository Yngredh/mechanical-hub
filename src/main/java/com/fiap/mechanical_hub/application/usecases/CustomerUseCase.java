package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.customer.UpsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.mappers.CustomerMapper;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentType;
import com.fiap.mechanical_hub.domain.exceptions.DuplicateDocumentException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidDocumentException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.application.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.mechanical_hub.shared.utils.Formatter.removeFormatting;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    public CustomerResponse create(UpsertCustomerRequest request) {
        log.info("Creating new customer");
        String cleanDocumentNumber = removeFormatting(request.getDocumentNumber());

        if (customerRepository.existsByDocumentNumber(cleanDocumentNumber)) {
            throw new DuplicateDocumentException(
                    String.format("Cliente com documento %s já existe", request.getDocumentNumber())
            );
        }

        Customer customer = customerMapper.toDomainEntity(request);
        Customer savedCustomer = customerRepository.save(customer);

        log.info("Customer created with id: {}", savedCustomer.getId());
        return customerMapper.toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        log.info("Retrieving customer with id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado para o id: " + id));
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        log.info("Retrieving all customers");
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    public CustomerResponse update(UUID id, UpsertCustomerRequest request) {
        log.info("Updating customer with id: {}", id);
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado para o id: " + id));

        String cleanRequestDocument = removeFormatting(request.getDocumentNumber());
        String cleanExistingDocument = existingCustomer.getDocumentNumber();

        if (!cleanExistingDocument.equals(cleanRequestDocument)) {
            throw new InvalidDocumentException("Não é permitido alterar o documento do cliente");
        }

        if (customerRepository.existsByDocumentNumberAndIdNot(cleanRequestDocument, id)) {
            throw new DuplicateDocumentException(
                    String.format("Cliente com documento %s já existe", request.getDocumentNumber()));
        }

        Customer updatedCustomer = customerMapper.toDomainEntity(request, existingCustomer);
        Customer savedCustomer = customerRepository.save(updatedCustomer);

        log.info("Customer with id: {} updated successfully", id);
        return customerMapper.toResponse(savedCustomer);
    }

    public void delete(UUID id) {
        log.info("Deleting customer with id: {}", id);
        if (customerRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Cliente não encontrado para o id: " + id);
        }
        customerRepository.deleteById(id);
    }

    public Customer findByDocumentOrCreate(String name, String documentType, String documentNumber,
                                           String telephone, String email, String address) {
        String cleanDocumentNumber = removeFormatting(documentNumber);

        Optional<Customer> existingCustomer = customerRepository.findByDocumentNumber(cleanDocumentNumber);
        if (existingCustomer.isPresent()) {
            return existingCustomer.get();
        }

        DocumentType type = DocumentType.fromValue(documentType);
        Customer newCustomer = Customer.create(name, type, documentNumber, telephone, email, address);
        return customerRepository.save(newCustomer);
    }
}


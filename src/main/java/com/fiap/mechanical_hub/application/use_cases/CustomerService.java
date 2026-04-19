package com.fiap.mechanical_hub.application.use_cases;

import com.fiap.mechanical_hub.application.dto.customer.UpsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.mappers.CustomerMapper;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentType;
import com.fiap.mechanical_hub.domain.exceptions.DuplicateDocumentException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidDocumentException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.mechanical_hub.shared.utils.Formatter.removeFormatting;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    public CustomerResponse create(UpsertCustomerRequest request) {
        String cleanDocumentNumber = removeFormatting(request.getDocumentNumber());

        if (customerRepository.existsByDocumentNumber(cleanDocumentNumber)) {
            throw new DuplicateDocumentException(
                    String.format("Cliente com documento %s já existe", request.getDocumentNumber())
            );
        }

        Customer customer = customerMapper.toDomainEntity(request);
        Customer savedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado para o id: " + id));
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
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado para o id: " + id));

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

        return customerMapper.toResponse(savedCustomer);
    }

    public void delete(UUID id) {
        if (customerRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Cliente não encontrado para o id: " + id);
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


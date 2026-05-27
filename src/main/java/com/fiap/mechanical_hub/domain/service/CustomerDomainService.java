package com.fiap.mechanical_hub.domain.service;

import com.fiap.mechanical_hub.domain.exceptions.DuplicatedDocumentException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerDomainService {

    private final CustomerRepository repository;

    public void validateUniqueDocument(Document document) {
        if (repository.existsByDocumentNumber(document.getNumber())) {
            throw new DuplicatedDocumentException("Documento duplicado");
        }
    }

}
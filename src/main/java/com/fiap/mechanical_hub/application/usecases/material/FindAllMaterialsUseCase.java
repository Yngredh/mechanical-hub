package com.fiap.mechanical_hub.application.usecases.material;

import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.application.mappers.MaterialMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindAllMaterialsUseCase {

    private final MaterialRepository repository;

    @Transactional(readOnly = true)
    public List<MaterialResponse> execute() {
        log.info("Finding all materials");
        return repository.findAll().stream()
                .map(MaterialMapper::toResponse)
                .toList();
    }

}


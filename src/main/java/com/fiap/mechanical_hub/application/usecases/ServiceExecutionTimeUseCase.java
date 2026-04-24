package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.reports.ServiceExecutionTimeResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceExecutionTimeMapper;
import com.fiap.mechanical_hub.infrastructure.database.repositories.OrderTaskJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceExecutionTimeUseCase {

    private final OrderTaskJpaRepository orderTaskRepository;

    public List<ServiceExecutionTimeResponse> getAverageExecutionTime() {
        return orderTaskRepository.findAverageExecutionTimeByService()
                .stream()
                .map(ServiceExecutionTimeMapper::toResponse)
                .toList();
    }
}


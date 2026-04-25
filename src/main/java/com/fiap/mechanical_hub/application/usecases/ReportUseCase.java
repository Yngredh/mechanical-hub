package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.reports.ServiceExecutionTimeResponse;
import com.fiap.mechanical_hub.application.mappers.ReportsMapper;
import com.fiap.mechanical_hub.application.repositories.OrderTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportUseCase {

    private final OrderTaskRepository orderTaskRepository;

    public List<ServiceExecutionTimeResponse> getAverageExecutionTime() {
        return orderTaskRepository.findAverageExecutionTimeByService()
                .stream()
                .map(ReportsMapper::toResponse)
                .toList();
    }
}


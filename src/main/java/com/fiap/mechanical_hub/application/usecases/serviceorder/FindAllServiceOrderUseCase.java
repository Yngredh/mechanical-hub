package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindAllServiceOrderUseCase {

    private final ServiceOrderRepository repository;

    @Transactional(readOnly = true)
    public List<ServiceOrderSummaryResponse> execute() {
        log.info("Retrieving all service orders");
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(order -> new ServiceOrderSummaryResponse(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getStatus().getDisplayName(),
                        order.isHasStockPending(),
                        order.getCreatedAt()
                )).toList();
    }
}


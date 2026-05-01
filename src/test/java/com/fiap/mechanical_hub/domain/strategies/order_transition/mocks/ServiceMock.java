package com.fiap.mechanical_hub.domain.strategies.order_transition.mocks;

import com.fiap.mechanical_hub.domain.entities.ServiceData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

public final class ServiceMock {

    private ServiceMock() {
    }

    public static ServiceData serviceWithId(UUID id) {
        return new ServiceData(
                id,
                "Service name",
                "Service description",
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                Collections.emptyList(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}


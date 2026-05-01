package com.fiap.mechanical_hub.domain.strategies.order_transition.constants;

import java.math.BigDecimal;
import java.util.UUID;

public final class TestConstants {

    private TestConstants() {
    }

    public static final UUID DEFAULT_SERVICE_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    public static final BigDecimal DEFAULT_BUDGET = new BigDecimal("100.00");
    public static final BigDecimal ZERO_BUDGET = BigDecimal.ZERO;
}


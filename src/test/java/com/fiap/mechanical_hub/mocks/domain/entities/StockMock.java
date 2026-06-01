package com.fiap.mechanical_hub.mocks.domain.entities;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import java.time.LocalDateTime;
import java.util.UUID;

public class StockMock {

    private static final UUID STOCK_ID = UUID.fromString("00000000-0000-0000-0000-000000000030");
    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    public static Stock available(int quantity) {
        return new Stock(
                STOCK_ID,
                MATERIAL_ID,
                quantity,
                StockStatusEnum.AVAILABLE,
                LocalDateTime.now()
        );
    }

    public static Stock reserved(int quantity) {
        return new Stock(
                STOCK_ID,
                MATERIAL_ID,
                quantity,
                StockStatusEnum.RESERVED,
                LocalDateTime.now()
        );
    }

    public static Stock empty() {
        return new Stock(
                STOCK_ID,
                MATERIAL_ID,
                0,
                StockStatusEnum.AVAILABLE,
                LocalDateTime.now()
        );
    }

    public static Stock belowMinimum() {
        return new Stock(
                STOCK_ID,
                MATERIAL_ID,
                5,
                StockStatusEnum.AVAILABLE,
                LocalDateTime.now()
        );
    }
}


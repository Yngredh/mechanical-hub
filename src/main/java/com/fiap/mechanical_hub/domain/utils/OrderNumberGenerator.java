package com.fiap.mechanical_hub.domain.utils;

import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class OrderNumberGenerator {

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    private final ServiceOrderRepository serviceOrderRepository;

    public synchronized String generate() {
        String yearMonth = LocalDate.now().format(YEAR_MONTH_FORMATTER);

        int nextSequence = serviceOrderRepository
                .findLastOrderNumberByYearMonth(yearMonth)
                .map(last -> extractSequence(last) + 1)
                .orElse(1);

        return String.format("OS-%s-%04d", yearMonth, nextSequence);
    }

    private int extractSequence(String orderNumber) {
        String[] parts = orderNumber.split("-");
        return Integer.parseInt(parts[2]);
    }
}

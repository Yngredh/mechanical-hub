package com.fiap.mechanical_hub.domain.utils;

import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderNumberGeneratorTest {

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private static final String CURRENT_YEAR_MONTH = LocalDate.now().format(YEAR_MONTH_FORMATTER);

    private final ServiceOrderRepository serviceOrderRepository = mock(ServiceOrderRepository.class);
    private final OrderNumberGenerator generator = new OrderNumberGenerator(serviceOrderRepository);

    @Test
    void shouldGenerateOrderNumberStartingAtOne_whenNoOrderExistsForCurrentMonth() {
        when(serviceOrderRepository.findLastOrderNumberByYearMonth(anyString()))
                .thenReturn(Optional.empty());

        String result = generator.generate();

        assertThat(result).isEqualTo("OS-" + CURRENT_YEAR_MONTH + "-0001");
    }

    @Test
    void shouldIncrementSequence_whenAnOrderAlreadyExistsForCurrentMonth() {
        String lastOrderNumber = "OS-" + CURRENT_YEAR_MONTH + "-0005";
        when(serviceOrderRepository.findLastOrderNumberByYearMonth(anyString()))
                .thenReturn(Optional.of(lastOrderNumber));

        String result = generator.generate();

        assertThat(result).isEqualTo("OS-" + CURRENT_YEAR_MONTH + "-0006");
    }

    @Test
    void shouldPadSequenceWithLeadingZeros_whenSequenceIsLessThanFourDigits() {
        when(serviceOrderRepository.findLastOrderNumberByYearMonth(anyString()))
                .thenReturn(Optional.empty());

        String result = generator.generate();

        assertThat(result).matches("OS-\\d{6}-\\d{4}");
    }
}

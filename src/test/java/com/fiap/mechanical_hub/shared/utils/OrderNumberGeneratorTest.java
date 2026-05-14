package com.fiap.mechanical_hub.shared.utils;

import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.utils.OrderNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderNumberGeneratorTest {

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @InjectMocks
    private OrderNumberGenerator orderNumberGenerator;

    private String currentYearMonth;

    @BeforeEach
    void setUp() {
        currentYearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    @Test
    @DisplayName("Deve gerar o primeiro número da sequência quando não houver ordens anteriores")
    void generate_ShouldReturnFirstSequence_WhenNoPreviousOrders() {
        when(serviceOrderRepository.findLastOrderNumberByYearMonth(anyString()))
                .thenReturn(Optional.empty());

        String result = orderNumberGenerator.generate();

        String expected = String.format("OS-%s-0001", currentYearMonth);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve incrementar a sequência baseada no último número de ordem encontrado")
    void generate_ShouldIncrementSequence_WhenPreviousOrderExists() {
        String lastOrderNumber = String.format("OS-%s-0042", currentYearMonth);
        when(serviceOrderRepository.findLastOrderNumberByYearMonth(currentYearMonth))
                .thenReturn(Optional.of(lastOrderNumber));

        String result = orderNumberGenerator.generate();

        String expected = String.format("OS-%s-0043", currentYearMonth);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve extrair corretamente a sequência de números com mais de 4 dígitos se necessário")
    void generate_ShouldHandleLargeSequences() {
        String lastOrderNumber = String.format("OS-%s-9999", currentYearMonth);
        when(serviceOrderRepository.findLastOrderNumberByYearMonth(currentYearMonth))
                .thenReturn(Optional.of(lastOrderNumber));

        String result = orderNumberGenerator.generate();

        String expected = String.format("OS-%s-10000", currentYearMonth);
        assertEquals(expected, result);
    }
}
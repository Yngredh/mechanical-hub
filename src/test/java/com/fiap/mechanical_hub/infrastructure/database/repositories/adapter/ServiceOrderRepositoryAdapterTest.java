package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceOrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderRepositoryAdapterTest {

    @Mock
    private ServiceOrderJpaRepository jpaRepository;

    private ServiceOrderRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ServiceOrderRepositoryAdapter(jpaRepository);
    }


    @Test
    void testFindAllSummaries_ValidDateRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        when(jpaRepository.findAllSummaries(any(), any(), any(), any())).thenReturn(java.util.List.of());

        // Act
        adapter.findAllSummaries(null, null, startDate, endDate);

        // Assert
        verify(jpaRepository).findAllSummaries(null, null, startDate, endDate);
    }
}

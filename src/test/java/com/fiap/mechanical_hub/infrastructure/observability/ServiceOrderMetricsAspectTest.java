package com.fiap.mechanical_hub.infrastructure.observability;

import com.fiap.mechanical_hub.infrastructure.integrations.email.EmailSender;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServiceOrderMetricsAspectTest {

    private SimpleMeterRegistry registry;
    private ServiceOrderMetrics metrics;
    private ServiceOrderMetricsAspect aspect;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new ServiceOrderMetrics(registry);
        aspect = new ServiceOrderMetricsAspect(metrics);
    }

    @Test
    void shouldCountCreatedOrder() {
        aspect.countCreatedOrder();

        Counter counter = registry.find(ServiceOrderMetrics.ORDERS_CREATED).counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    /**
     * A etiqueta vem do pacote, e nao do nome da classe, para que renomear
     * {@code EmailSender} nao mude a serie e nao quebre o painel de erros de
     * integracao no meio do historico.
     */
    @Test
    void shouldNameIntegrationAfterItsPackage() {
        JoinPoint joinPoint = joinPointOn(EmailSender.class);

        assertThat(ServiceOrderMetricsAspect.integrationNameOf(joinPoint)).isEqualTo("email");
    }

    @Test
    void shouldCountIntegrationFailureWithExceptionType() {
        JoinPoint joinPoint = joinPointOn(EmailSender.class);

        aspect.countIntegrationFailure(joinPoint, new IllegalStateException("servico indisponivel"));

        Counter counter = registry.find(ServiceOrderMetrics.INTEGRATION_ERRORS)
                .tag("integration", "email")
                .tag("exception", "IllegalStateException")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    private static JoinPoint joinPointOn(Class<?> declaringType) {
        Signature signature = mock(Signature.class);
        when(signature.getDeclaringType()).thenAnswer(invocation -> declaringType);

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        return joinPoint;
    }
}

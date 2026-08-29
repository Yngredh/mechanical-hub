package com.fiap.mechanical_hub.infrastructure.observability;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.domain.strategies.order_transition.ApproveOrderTransition;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransitionFactory;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MeteredOrderStatusTransitionFactoryTest {

    private SimpleMeterRegistry registry;
    private MeteredOrderStatusTransitionFactory factory;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        ServiceOrderMetrics metrics = new ServiceOrderMetrics(registry);

        OrderStatusTransitionFactory delegate = new OrderStatusTransitionFactory(
                Map.of(OrderStatusEnum.APROVADO, new ApproveOrderTransition())
        );

        factory = new MeteredOrderStatusTransitionFactory(delegate, metrics);
    }

    /**
     * O decorador nao pode mudar o que a aplicacao faz — so acrescentar
     * observacao. Se a regra de negocio deixar de rodar, tudo o mais e
     * irrelevante.
     */
    @Test
    void shouldApplyTheDomainTransitionUnchanged() {
        ServiceOrder order = ServiceOrderMock.waitingApproval();

        factory.get(OrderStatusEnum.APROVADO).execute(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.APROVADO);
    }

    @Test
    void shouldCountSuccessfulTransition() {
        ServiceOrder order = ServiceOrderMock.waitingApproval();

        factory.get(OrderStatusEnum.APROVADO).execute(order);

        Counter counter = registry.find(ServiceOrderMetrics.TRANSITIONS)
                .tag("from", "AGUARDANDO_APROVACAO")
                .tag("to", "APROVADO")
                .tag("result", "success")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    /**
     * Transicao invalida precisa continuar estourando a excecao que os casos de
     * uso e o tratador global esperam. Contar o erro nao pode virar engolir o
     * erro.
     */
    @Test
    void shouldCountFailureAndRethrowTheOriginalException() {
        ServiceOrder order = ServiceOrderMock.received();

        assertThatThrownBy(() -> factory.get(OrderStatusEnum.APROVADO).execute(order))
                .isInstanceOf(InvalidOrderTransitionException.class);

        Counter counter = registry.find(ServiceOrderMetrics.TRANSITIONS)
                .tag("from", "RECEBIDO")
                .tag("to", "APROVADO")
                .tag("result", "error")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    /**
     * Status que o dominio nao conhece (hoje, EM_EXECUCAO) e recusado ja na
     * busca da transicao. Sem esta contagem, o caso sumiria dos paineis: a
     * excecao nasce antes de qualquer ordem ser tocada.
     */
    @Test
    void shouldCountRequestForUnsupportedStatus() {
        assertThatThrownBy(() -> factory.get(OrderStatusEnum.EM_EXECUCAO))
                .isInstanceOf(IllegalArgumentException.class);

        Counter counter = registry.find(ServiceOrderMetrics.TRANSITIONS)
                .tag("from", "unknown")
                .tag("to", "EM_EXECUCAO")
                .tag("result", "error")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    /**
     * A superclasse recebe um mapa vazio de proposito. Este teste existe para
     * garantir que ninguem passe a depender do mapa herdado: toda busca precisa
     * continuar indo para a fabrica delegada.
     */
    @Test
    void shouldAlwaysDelegateInsteadOfUsingTheInheritedMap() {
        assertThat(factory.get(OrderStatusEnum.APROVADO)).isNotNull();

        assertThatThrownBy(() -> factory.get(OrderStatusEnum.FINALIZADO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported transition");
    }
}

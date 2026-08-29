package com.fiap.mechanical_hub.infrastructure.observability;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceOrderMetricsTest {

    private SimpleMeterRegistry registry;
    private ServiceOrderMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new ServiceOrderMetrics(registry);
    }

    @Test
    void shouldCountCreatedOrders() {
        metrics.orderCreated();
        metrics.orderCreated();

        Counter counter = registry.find(ServiceOrderMetrics.ORDERS_CREATED).counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    void shouldTagSuccessfulTransitionWithOriginDestinationAndResult() {
        ServiceOrder order = ServiceOrderMock.waitingApproval();

        metrics.transitionSucceeded(OrderStatusEnum.AGUARDANDO_APROVACAO, OrderStatusEnum.APROVADO, order);

        Counter counter = registry.find(ServiceOrderMetrics.TRANSITIONS)
                .tag("from", "AGUARDANDO_APROVACAO")
                .tag("to", "APROVADO")
                .tag("result", "success")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void shouldTagFailedTransitionAsError() {
        metrics.transitionFailed(OrderStatusEnum.RECEBIDO, OrderStatusEnum.FINALIZADO);

        Counter counter = registry.find(ServiceOrderMetrics.TRANSITIONS)
                .tag("from", "RECEBIDO")
                .tag("to", "FINALIZADO")
                .tag("result", "error")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    /**
     * Uma transicao recusada porque o status pedido nao existe no dominio chega
     * aqui sem origem conhecida. Precisa continuar sendo contada — e justamente
     * o caso que o painel de erros existe para mostrar.
     */
    @Test
    void shouldCountTransitionWithUnknownOrigin() {
        metrics.transitionFailed(null, OrderStatusEnum.EM_EXECUCAO);

        Counter counter = registry.find(ServiceOrderMetrics.TRANSITIONS)
                .tag("from", "unknown")
                .tag("to", "EM_EXECUCAO")
                .tag("result", "error")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    /**
     * A duracao em RECEBIDO sai de {@code createdAt} e {@code openedAt}, ambos
     * persistidos na entidade. E o que faz esse numero continuar correto depois
     * de um restart do pod, sem depender de estado em memoria.
     */
    @Test
    void shouldMeasureTimeInReceivedFromPersistedTimestamps() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();

        metrics.transitionSucceeded(OrderStatusEnum.RECEBIDO, OrderStatusEnum.EM_DIAGNOSTICO, order);

        Timer timer = registry.find(ServiceOrderMetrics.STATUS_DURATION)
                .tag("status", "RECEBIDO")
                .timer();

        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1L);
    }

    /**
     * Sem uma transicao anterior observada e sem carimbo na entidade, nao ha de
     * onde tirar a duracao. Nao registrar nada e melhor do que registrar zero,
     * que puxaria a media do painel para baixo com um valor inventado.
     */
    @Test
    void shouldNotRecordDurationWhenEntryInstantIsUnknown() {
        ServiceOrder order = ServiceOrderMock.approvedWithoutStockPending();

        metrics.transitionSucceeded(OrderStatusEnum.APROVADO, OrderStatusEnum.EM_EXECUCAO, order);

        Timer timer = registry.find(ServiceOrderMetrics.STATUS_DURATION)
                .tag("status", "APROVADO")
                .timer();

        assertThat(timer).isNull();
    }

    /**
     * Quando a propria aplicacao observou a entrada no status, a duracao passa a
     * ser medida mesmo para os status que a entidade nao carimba.
     */
    @Test
    void shouldMeasureStatusObservedByTheApplicationItself() {
        ServiceOrder order = ServiceOrderMock.waitingApproval();

        // Primeira transicao: a aplicacao registra o momento de entrada em APROVADO.
        metrics.transitionSucceeded(OrderStatusEnum.AGUARDANDO_APROVACAO, OrderStatusEnum.APROVADO, order);
        // Segunda: agora ha de onde medir quanto tempo ficou em APROVADO.
        metrics.transitionSucceeded(OrderStatusEnum.APROVADO, OrderStatusEnum.EM_EXECUCAO, order);

        Timer timer = registry.find(ServiceOrderMetrics.STATUS_DURATION)
                .tag("status", "APROVADO")
                .timer();

        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1L);
    }

    @Test
    void shouldCountIntegrationFailuresByIntegrationName() {
        metrics.integrationFailed("email", "ConnectException");

        Counter counter = registry.find(ServiceOrderMetrics.INTEGRATION_ERRORS)
                .tag("integration", "email")
                .tag("exception", "ConnectException")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void shouldFallBackToUnknownInsteadOfEmptyTag() {
        metrics.integrationFailed("  ", null);

        Counter counter = registry.find(ServiceOrderMetrics.INTEGRATION_ERRORS)
                .tag("integration", "unknown")
                .tag("exception", "unknown")
                .counter();

        assertThat(counter).isNotNull();
    }

    /**
     * O mapa de status observados vive num processo de vida longa. Sem limite,
     * ele cresceria a cada ordem atendida ate consumir a heap — um vazamento
     * que so apareceria depois de dias no ar.
     */
    @Test
    void shouldNotGrowTrackedOrdersWithoutLimit() {
        int excess = ServiceOrderMetrics.MAX_TRACKED_ORDERS + 500;

        for (int i = 0; i < excess; i++) {
            ServiceOrder order = ServiceOrderMock.waitingApproval();
            metrics.transitionSucceeded(OrderStatusEnum.AGUARDANDO_APROVACAO, OrderStatusEnum.APROVADO, order);
        }

        Counter counter = registry.find(ServiceOrderMetrics.TRANSITIONS)
                .tag("result", "success")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo((double) excess);
        assertThat(metrics.trackedOrders()).isLessThanOrEqualTo(ServiceOrderMetrics.MAX_TRACKED_ORDERS);
    }

    @Test
    void shouldNotFailWhenOrderIsNull() {
        metrics.transitionSucceeded(OrderStatusEnum.RECEBIDO, OrderStatusEnum.EM_DIAGNOSTICO, null);

        Counter counter = registry.find(ServiceOrderMetrics.TRANSITIONS)
                .tag("result", "success")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }
}

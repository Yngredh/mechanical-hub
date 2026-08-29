package com.fiap.mechanical_hub.infrastructure.observability;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
// Pacote `io.micrometer.prometheus` (e nao `prometheusmetrics`): e o que o
// Micrometer 1.12, trazido pelo Spring Boot 3.2.5, publica. O pacote
// `prometheusmetrics` so aparece a partir do Micrometer 1.13.
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contrato de nomes com o repositorio {@code mechanical-hub-infra}.
 *
 * <p>Os dashboards e as regras de alerta referenciam estas series pelo nome
 * exato. Renomear qualquer uma delas nao quebra a compilacao, nao gera erro no
 * deploy e nao aparece em log nenhum: o painel simplesmente fica vazio e o
 * alerta nunca dispara. Este teste transforma essa falha silenciosa em uma
 * falha de build.
 *
 * <p>Os nomes abaixo estao escritos como o Prometheus os enxerga — e nao como o
 * Micrometer os declara — justamente porque e essa a forma que os paineis usam.
 * A traducao entre as duas (pontos viram underscore, contador ganha
 * {@code _total}, temporizador ganha {@code _seconds}) e o que este teste
 * verifica de fato.
 *
 * <p>Ao alterar qualquer nome aqui, altere junto:
 * <ul>
 *   <li>{@code infra/modules/observability/dashboards/*.json}</li>
 *   <li>{@code infra/modules/observability/values/kube-prometheus-stack.yaml.tftpl}
 *       (bloco {@code additionalPrometheusRulesMap})</li>
 * </ul>
 */
class MetricsContractTest {

    private PrometheusMeterRegistry registry;
    private ServiceOrderMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        metrics = new ServiceOrderMetrics(registry);
    }

    @Test
    void shouldPublishCreatedOrdersWithTheNameTheDashboardExpects() {
        metrics.orderCreated();

        assertThat(registry.scrape())
                .contains("mechanical_hub_service_orders_created_total");
    }

    @Test
    void shouldPublishTransitionsWithTheNameAndTagsTheAlertExpects() {
        ServiceOrder order = ServiceOrderMock.waitingApproval();
        metrics.transitionSucceeded(OrderStatusEnum.AGUARDANDO_APROVACAO, OrderStatusEnum.APROVADO, order);
        metrics.transitionFailed(OrderStatusEnum.RECEBIDO, OrderStatusEnum.ENTREGUE);

        String scrape = registry.scrape();

        assertThat(scrape).contains("mechanical_hub_service_order_transitions_total");
        // O alerta MechanicalHubFalhaProcessamentoOS filtra por result="error";
        // o painel de OS entregues, por to="ENTREGUE".
        assertThat(scrape).contains("result=\"error\"");
        assertThat(scrape).contains("result=\"success\"");
        assertThat(scrape).contains("to=\"ENTREGUE\"");
        assertThat(scrape).contains("from=\"AGUARDANDO_APROVACAO\"");
    }

    /**
     * O painel de p95 de permanencia por status usa
     * {@code histogram_quantile(...)}, que so funciona com a serie
     * {@code _bucket}. Um temporizador sem histograma publica apenas
     * {@code _count} e {@code _sum} — e o painel fica vazio sem qualquer sinal
     * de erro.
     */
    @Test
    void shouldPublishStatusDurationAsHistogramWithBuckets() {
        ServiceOrder order = ServiceOrderMock.inDiagnosis();
        metrics.transitionSucceeded(OrderStatusEnum.RECEBIDO, OrderStatusEnum.EM_DIAGNOSTICO, order);

        String scrape = registry.scrape();

        assertThat(scrape).contains("mechanical_hub_service_order_status_duration_seconds_bucket");
        assertThat(scrape).contains("mechanical_hub_service_order_status_duration_seconds_count");
        assertThat(scrape).contains("mechanical_hub_service_order_status_duration_seconds_sum");
        assertThat(scrape).contains("status=\"RECEBIDO\"");
    }

    @Test
    void shouldPublishIntegrationErrorsWithTheNameTheDashboardExpects() {
        metrics.integrationFailed("whatsapp", "TimeoutException");

        String scrape = registry.scrape();

        assertThat(scrape).contains("mechanical_hub_integration_errors_total");
        assertThat(scrape).contains("integration=\"whatsapp\"");
    }
}

package com.fiap.mechanical_hub.infrastructure.observability;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Publica as metricas de negocio de ordem de servico.
 *
 * <p>Os nomes aqui sao <b>contrato</b>: os dashboards e as regras de alerta do
 * repositorio {@code mechanical-hub-infra} referenciam estas series
 * literalmente. Renomear qualquer uma esvazia um painel sem gerar erro em lugar
 * nenhum — por isso os nomes estao em constantes e cobertos por teste.
 *
 * <table>
 *   <caption>Series publicadas</caption>
 *   <tr><td>{@code mechanical_hub_service_orders_created_total}</td>
 *       <td>ordens abertas</td></tr>
 *   <tr><td>{@code mechanical_hub_service_order_transitions_total}</td>
 *       <td>mudancas de status, com {@code from}/{@code to}/{@code result}</td></tr>
 *   <tr><td>{@code mechanical_hub_service_order_status_duration_seconds}</td>
 *       <td>tempo de permanencia em cada status</td></tr>
 *   <tr><td>{@code mechanical_hub_integration_errors_total}</td>
 *       <td>falhas nas integracoes externas</td></tr>
 * </table>
 */
@Component
public class ServiceOrderMetrics {

    // O Micrometer converte pontos em underscore e acrescenta o sufixo da
    // unidade ao publicar no formato do Prometheus: `...created` vira
    // `mechanical_hub_service_orders_created_total`.
    static final String ORDERS_CREATED = "mechanical_hub.service_orders.created";
    static final String TRANSITIONS = "mechanical_hub.service_order.transitions";
    static final String STATUS_DURATION = "mechanical_hub.service_order.status_duration";
    static final String INTEGRATION_ERRORS = "mechanical_hub.integration.errors";

    static final String RESULT_SUCCESS = "success";
    static final String RESULT_ERROR = "error";
    static final String UNKNOWN = "unknown";

    /**
     * Limite do mapa de status em memoria. Ver {@link #statusEnteredAt}: o mapa
     * so cobre os status que a entidade nao carimba no banco, e existe para nao
     * crescer sem limite num processo de vida longa.
     */
    static final int MAX_TRACKED_ORDERS = 10_000;

    private final MeterRegistry registry;

    /**
     * Momento em que cada ordem entrou no status atual, para os status que a
     * entidade nao persiste. Acesso sincronizado porque varias requisicoes
     * mexem nele em paralelo.
     */
    private final Map<UUID, Instant> statusEnteredAt;

    public ServiceOrderMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.statusEnteredAt = Collections.synchronizedMap(
                new LinkedHashMap<UUID, Instant>(256, 0.75f, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<UUID, Instant> eldest) {
                        return size() > MAX_TRACKED_ORDERS;
                    }
                }
        );
    }

    /** Uma ordem de servico foi aberta. */
    public void orderCreated() {
        Counter.builder(ORDERS_CREATED)
                .description("Ordens de servico abertas")
                .register(registry)
                .increment();
    }

    /**
     * Uma mudanca de status foi concluida com sucesso.
     *
     * @param from   status antes da transicao
     * @param to     status depois da transicao
     * @param order  a ordem, para medir quanto tempo ela ficou no status anterior
     */
    public void transitionSucceeded(OrderStatusEnum from, OrderStatusEnum to, ServiceOrder order) {
        countTransition(name(from), name(to), RESULT_SUCCESS);
        recordStatusDuration(from, order);

        if (order != null && order.getId() != null) {
            statusEnteredAt.put(order.getId(), Instant.now());
        }
    }

    /**
     * Uma mudanca de status foi recusada — transicao invalida para o status
     * atual, regra de negocio violada ou falha inesperada.
     *
     * <p>E esta serie que alimenta o alerta {@code MechanicalHubFalhaProcessamentoOS}
     * e o painel de transicoes recusadas.
     */
    public void transitionFailed(OrderStatusEnum from, OrderStatusEnum to) {
        countTransition(name(from), name(to), RESULT_ERROR);
    }

    /** Uma integracao externa falhou. */
    public void integrationFailed(String integration, String exceptionType) {
        Counter.builder(INTEGRATION_ERRORS)
                .description("Falhas nas integracoes externas")
                .tag("integration", blankToUnknown(integration))
                .tag("exception", blankToUnknown(exceptionType))
                .register(registry)
                .increment();
    }

    private void countTransition(String from, String to, String result) {
        Counter.builder(TRANSITIONS)
                .description("Mudancas de status de ordem de servico")
                .tag("from", from)
                .tag("to", to)
                .tag("result", result)
                .register(registry)
                .increment();
    }

    private void recordStatusDuration(OrderStatusEnum previousStatus, ServiceOrder order) {
        Instant enteredAt = statusEnteredAt(previousStatus, order);
        if (enteredAt == null) {
            return;
        }

        Duration elapsed = Duration.between(enteredAt, Instant.now());
        if (elapsed.isNegative()) {
            // Relogio do banco adiantado em relacao ao da aplicacao. Registrar
            // uma duracao negativa corromperia a media do painel.
            return;
        }

        Timer.builder(STATUS_DURATION)
                .description("Tempo de permanencia da ordem de servico em cada status")
                .tag("status", name(previousStatus))
                // Sem o histograma, o Prometheus recebe apenas _count e _sum, e
                // o histogram_quantile(0.95, ...) do painel de p95 nao retorna
                // nada.
                .publishPercentileHistogram()
                .register(registry)
                .record(elapsed.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Momento em que a ordem entrou no status informado.
     *
     * <p>Prefere sempre o que esta persistido na propria entidade: esses valores
     * continuam corretos depois de um restart do pod e sao os mesmos em qualquer
     * replica. O mapa em memoria e apenas o recurso final, para os status que a
     * entidade nao carimba — e por isso o tempo em {@code APROVADO} e
     * {@code EM_EXECUCAO} pode nao ser registrado quando a ordem troca de
     * replica ou o pod reinicia no meio do caminho.
     */
    private Instant statusEnteredAt(OrderStatusEnum status, ServiceOrder order) {
        if (order == null || status == null) {
            return null;
        }

        if (status == OrderStatusEnum.RECEBIDO) {
            return toInstant(order.getCreatedAt());
        }
        if (status == OrderStatusEnum.EM_DIAGNOSTICO) {
            return toInstant(order.getOpenedAt());
        }
        if (status == OrderStatusEnum.FINALIZADO) {
            return toInstant(order.getCompletedAt());
        }

        return order.getId() == null ? null : statusEnteredAt.get(order.getId());
    }

    /**
     * Converte um carimbo da entidade em instante.
     *
     * <p>Usa o fuso do sistema, e nao UTC, porque a entidade grava esses campos
     * com {@code LocalDateTime.now()} — que ja e hora local. Interpretar o valor
     * como UTC num ambiente em UTC-3 produziria uma duracao tres horas maior do
     * que a real, e o painel de tempo medio por status mostraria numeros
     * plausiveis, porem errados.
     */
    private static Instant toInstant(LocalDateTime value) {
        return value == null ? null : value.atZone(ZoneId.systemDefault()).toInstant();
    }

    /** Quantidade de ordens com entrada de status registrada em memoria. */
    int trackedOrders() {
        return statusEnteredAt.size();
    }

    private static String name(OrderStatusEnum status) {
        return status == null ? UNKNOWN : status.name();
    }

    private static String blankToUnknown(String value) {
        return (value == null || value.isBlank()) ? UNKNOWN : value;
    }
}

package com.fiap.mechanical_hub.infrastructure.observability;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransition;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransitionFactory;

import java.util.Map;

/**
 * Envolve a fabrica de transicoes do dominio para contar cada mudanca de status.
 *
 * <p><b>Por que aqui.</b> Todas as mudancas de status expostas pela API passam
 * por {@code factory.get(alvo).execute(ordem)} — o {@code PATCH /status}, a
 * aprovacao e a recusa do cliente. Instrumentar este unico ponto cobre todas
 * elas de uma vez, sem alterar nenhum caso de uso, nenhum construtor e, por
 * consequencia, nenhum teste existente. A regra de negocio segue intocada no
 * dominio; o que se acrescenta e observacao.
 *
 * <p><b>Cobertura.</b> Duas transicoes nao passam pela fabrica e por isso nao
 * sao contadas aqui:
 * <ul>
 *   <li>{@code RECEBIDO -> EM_DIAGNOSTICO} quando a ordem e aberta pelo
 *       {@code OpenServiceOrderUseCase}, que chama {@code startDiagnosis()}
 *       direto na entidade. Nesse fluxo a ordem nasce ja em diagnostico, e o
 *       evento relevante — a abertura — e contado pelo
 *       {@link ServiceOrderMetricsAspect}.</li>
 *   <li>{@code APROVADO -> EM_EXECUCAO}, disparada ao iniciar a primeira
 *       tarefa. {@code EM_EXECUCAO} nao esta no mapa de transicoes do dominio,
 *       entao pedir esse status pelo {@code PATCH /status} e recusado — e essa
 *       recusa e contada como erro.</li>
 * </ul>
 *
 * <p>A superclasse recebe um mapa vazio porque {@link #get(OrderStatusEnum)}
 * esta totalmente sobrescrito: nenhuma busca chega ao mapa herdado. Quem
 * responde e sempre a fabrica delegada.
 */
public class MeteredOrderStatusTransitionFactory extends OrderStatusTransitionFactory {

    private final OrderStatusTransitionFactory delegate;
    private final ServiceOrderMetrics metrics;

    public MeteredOrderStatusTransitionFactory(
            OrderStatusTransitionFactory delegate,
            ServiceOrderMetrics metrics
    ) {
        super(Map.of());
        this.delegate = delegate;
        this.metrics = metrics;
    }

    @Override
    public OrderStatusTransition get(OrderStatusEnum targetStatus) {
        OrderStatusTransition transition;
        try {
            transition = delegate.get(targetStatus);
        } catch (RuntimeException e) {
            // Status pedido que o dominio nao suporta (hoje: EM_EXECUCAO).
            // Contar aqui e o que faz esse caso aparecer no painel de erros em
            // vez de sumir como um 4xx qualquer.
            metrics.transitionFailed(null, targetStatus);
            throw e;
        }

        return order -> {
            OrderStatusEnum from = order == null ? null : order.getStatus();
            try {
                transition.execute(order);
                metrics.transitionSucceeded(from, statusOf(order, targetStatus), order);
            } catch (RuntimeException e) {
                metrics.transitionFailed(from, targetStatus);
                throw e;
            }
        };
    }

    /**
     * Status real depois da transicao. Prefere o que ficou na entidade ao alvo
     * pedido: sao iguais no caminho normal, mas o valor da entidade e o que de
     * fato aconteceu.
     */
    private static OrderStatusEnum statusOf(ServiceOrder order, OrderStatusEnum fallback) {
        if (order == null || order.getStatus() == null) {
            return fallback;
        }
        return order.getStatus();
    }
}

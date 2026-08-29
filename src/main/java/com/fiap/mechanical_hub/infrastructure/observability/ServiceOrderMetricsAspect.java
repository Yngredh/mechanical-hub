package com.fiap.mechanical_hub.infrastructure.observability;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Instrumentacao que nao cabe na fabrica de transicoes.
 *
 * <p>Usa aspectos por uma razao concreta: injetar um contador diretamente nos
 * casos de uso mudaria o construtor de cada um deles e quebraria os testes
 * unitarios que os instanciam a mao — um custo alto para acrescentar apenas
 * observacao. Com o aspecto, {@code application/} e {@code domain/} continuam
 * sem qualquer referencia a Micrometer, respeitando a direcao de dependencia da
 * arquitetura do projeto.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ServiceOrderMetricsAspect {

    private final ServiceOrderMetrics metrics;

    /**
     * Conta uma ordem de servico aberta.
     *
     * <p>Sao os dois unicos caminhos de criacao da aplicacao: {@code POST
     * /service-orders} e {@code POST /service-orders/open}. Como e
     * {@code @AfterReturning}, uma criacao que falhou (cliente inexistente,
     * veiculo de outro dono, descricao invalida) nao entra na conta — o painel
     * mostra ordens que existem, nao tentativas.
     */
    @AfterReturning(
            "execution(* com.fiap.mechanical_hub.application.usecases.serviceorder.CreateServiceOrderUseCase.execute(..)) "
                    + "|| execution(* com.fiap.mechanical_hub.application.usecases.serviceorder.OpenServiceOrderUseCase.execute(..))"
    )
    public void countCreatedOrder() {
        metrics.orderCreated();
    }

    /**
     * Conta falhas das integracoes externas (e-mail, WhatsApp).
     *
     * <p>O ponto de corte cobre o pacote inteiro: uma integracao nova adicionada
     * depois ja nasce instrumentada, sem ninguem precisar lembrar de voltar
     * aqui. O nome da integracao vem do pacote em que a classe vive, e nao do
     * nome dela, para que a etiqueta continue estavel se a classe for renomeada.
     */
    @AfterThrowing(
            pointcut = "execution(* com.fiap.mechanical_hub.infrastructure.integrations..*.*(..))",
            throwing = "error"
    )
    public void countIntegrationFailure(JoinPoint joinPoint, Throwable error) {
        metrics.integrationFailed(
                integrationNameOf(joinPoint),
                error.getClass().getSimpleName()
        );
    }

    /**
     * Extrai o nome da integracao do pacote da classe: de
     * {@code ...infrastructure.integrations.email.EmailSender} sai
     * {@code email}.
     */
    static String integrationNameOf(JoinPoint joinPoint) {
        String packageName = joinPoint.getSignature().getDeclaringType().getPackage().getName();
        int lastDot = packageName.lastIndexOf('.');
        return lastDot < 0 ? packageName : packageName.substring(lastDot + 1);
    }
}

package com.fiap.mechanical_hub.infrastructure.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Coloca no MDC o contexto da requisicao, para que ele apareca em toda linha de
 * log gerada durante o atendimento dela.
 *
 * <p>O {@code trace_id} e o {@code span_id} nao sao responsabilidade desta
 * classe: quem os coloca no MDC e o micrometer-tracing-bridge-otel. O que falta
 * — e o que esta classe adiciona — e a identidade de quem fez a chamada, que
 * chega nos cabecalhos escritos pelo API Gateway.
 *
 * <p>Isso e o que transforma "houve um erro" em "houve um erro nesta requisicao,
 * feita por este funcionario, nesta rota" sem precisar que cada ponto de log
 * repita esses dados.
 *
 * <p>Roda antes do {@code GatewayAuthenticationFilter} de proposito: se a
 * autenticacao rejeitar a requisicao, o log dessa rejeicao ja sai com o
 * contexto preenchido.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class LogCorrelationFilter extends OncePerRequestFilter {

    static final String USER_ID_HEADER = "x-user-id";
    static final String USER_ROLE_HEADER = "x-user-role";

    static final String USER_ID_KEY = "user_id";
    static final String USER_ROLE_KEY = "user_role";
    static final String HTTP_METHOD_KEY = "http_method";
    static final String HTTP_PATH_KEY = "http_path";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            put(HTTP_METHOD_KEY, request.getMethod());
            put(HTTP_PATH_KEY, request.getRequestURI());
            put(USER_ID_KEY, request.getHeader(USER_ID_HEADER));
            put(USER_ROLE_KEY, request.getHeader(USER_ROLE_HEADER));

            filterChain.doFilter(request, response);
        } finally {
            // O bloco finally nao e detalhe: as threads do servidor sao
            // reaproveitadas entre requisicoes. Sem a limpeza, o contexto de um
            // usuario vazaria para o log da requisicao seguinte, de outro
            // usuario, na mesma thread.
            MDC.remove(HTTP_METHOD_KEY);
            MDC.remove(HTTP_PATH_KEY);
            MDC.remove(USER_ID_KEY);
            MDC.remove(USER_ROLE_KEY);
        }
    }

    private void put(String key, String value) {
        if (value != null && !value.isBlank()) {
            MDC.put(key, value);
        }
    }
}

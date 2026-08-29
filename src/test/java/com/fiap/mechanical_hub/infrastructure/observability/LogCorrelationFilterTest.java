package com.fiap.mechanical_hub.infrastructure.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LogCorrelationFilterTest {

    private final LogCorrelationFilter filter = new LogCorrelationFilter();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void shouldExposeRequestContextToLogsDuringTheChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/service-orders");
        request.addHeader(LogCorrelationFilter.USER_ID_HEADER, "11111111-1111-1111-1111-111111111111");
        request.addHeader(LogCorrelationFilter.USER_ROLE_HEADER, "ADMINISTRATOR");

        Map<String, String> seenInsideChain = new HashMap<>();
        FilterChain chain = (req, res) -> {
            seenInsideChain.put(LogCorrelationFilter.USER_ID_KEY, MDC.get(LogCorrelationFilter.USER_ID_KEY));
            seenInsideChain.put(LogCorrelationFilter.USER_ROLE_KEY, MDC.get(LogCorrelationFilter.USER_ROLE_KEY));
            seenInsideChain.put(LogCorrelationFilter.HTTP_METHOD_KEY, MDC.get(LogCorrelationFilter.HTTP_METHOD_KEY));
            seenInsideChain.put(LogCorrelationFilter.HTTP_PATH_KEY, MDC.get(LogCorrelationFilter.HTTP_PATH_KEY));
        };

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertThat(seenInsideChain)
                .containsEntry(LogCorrelationFilter.USER_ID_KEY, "11111111-1111-1111-1111-111111111111")
                .containsEntry(LogCorrelationFilter.USER_ROLE_KEY, "ADMINISTRATOR")
                .containsEntry(LogCorrelationFilter.HTTP_METHOD_KEY, "POST")
                .containsEntry(LogCorrelationFilter.HTTP_PATH_KEY, "/service-orders");
    }

    /**
     * As threads do servidor sao reaproveitadas entre requisicoes. Sem limpeza,
     * o identificador de um funcionario apareceria nos logs da requisicao
     * seguinte, de outra pessoa — um vazamento silencioso de dado de usuario.
     */
    @Test
    void shouldClearContextAfterTheRequest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/service-orders");
        request.addHeader(LogCorrelationFilter.USER_ID_HEADER, "22222222-2222-2222-2222-222222222222");

        filter.doFilter(request, new MockHttpServletResponse(), (req, res) -> { });

        assertThat(MDC.get(LogCorrelationFilter.USER_ID_KEY)).isNull();
        assertThat(MDC.get(LogCorrelationFilter.HTTP_METHOD_KEY)).isNull();
        assertThat(MDC.get(LogCorrelationFilter.HTTP_PATH_KEY)).isNull();
    }

    /** A limpeza tambem precisa acontecer quando a requisicao termina em erro. */
    @Test
    void shouldClearContextWhenTheRequestFails() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/service-orders");
        request.addHeader(LogCorrelationFilter.USER_ID_HEADER, "33333333-3333-3333-3333-333333333333");

        FilterChain failing = (req, res) -> {
            throw new IllegalStateException("falha durante a requisicao");
        };

        assertThatThrownBy(() -> filter.doFilter(request, new MockHttpServletResponse(), failing))
                .isInstanceOf(IllegalStateException.class);

        assertThat(MDC.get(LogCorrelationFilter.USER_ID_KEY)).isNull();
    }

    /**
     * As rotas publicas do cliente final chegam sem cabecalho de usuario. O
     * contexto disponivel deve ser preenchido mesmo assim, e as chaves ausentes
     * devem ficar de fora em vez de virarem string vazia no log.
     */
    @Test
    void shouldWorkOnPublicRoutesWithoutUserHeaders() throws ServletException, IOException {
        MockHttpServletRequest request =
                new MockHttpServletRequest("POST", "/mechanical-hub/service-orders/123/approve");

        Map<String, String> seenInsideChain = new HashMap<>();
        FilterChain chain = (req, res) -> {
            seenInsideChain.put("path", MDC.get(LogCorrelationFilter.HTTP_PATH_KEY));
            seenInsideChain.put("user", MDC.get(LogCorrelationFilter.USER_ID_KEY));
        };

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertThat(seenInsideChain.get("path")).isEqualTo("/mechanical-hub/service-orders/123/approve");
        assertThat(seenInsideChain.get("user")).isNull();
    }
}

package com.fiap.mechanical_hub.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GatewayAuthenticationFilterTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");

    private final GatewayAuthenticationFilter filter = new GatewayAuthenticationFilter();

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final FilterChain chain = mock(FilterChain.class);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void invokeFilter() throws Exception {
        Method method = GatewayAuthenticationFilter.class.getDeclaredMethod(
                "doFilterInternal", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        method.invoke(filter, request, response, chain);
    }

    private Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Test
    void shouldAuthenticate_whenGatewayHeadersArePresent() throws Exception {
        when(request.getHeader(GatewayAuthenticationFilter.USER_ID_HEADER)).thenReturn(USER_ID.toString());
        when(request.getHeader(GatewayAuthenticationFilter.USER_ROLE_HEADER)).thenReturn("ADMINISTRATOR");
        when(request.getHeader(GatewayAuthenticationFilter.USER_NAME_HEADER)).thenReturn("João Silva");

        invokeFilter();

        assertThat(currentAuthentication()).isNotNull();
        assertThat(currentAuthentication().getPrincipal())
                .isEqualTo(new GatewayPrincipal(USER_ID, "João Silva", "ADMINISTRATOR"));
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldGrantRoleWithSpringPrefix_whenAuthenticating() throws Exception {
        when(request.getHeader(GatewayAuthenticationFilter.USER_ID_HEADER)).thenReturn(USER_ID.toString());
        when(request.getHeader(GatewayAuthenticationFilter.USER_ROLE_HEADER)).thenReturn("MECHANICAL");

        invokeFilter();

        assertThat(currentAuthentication().getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_MECHANICAL");
    }

    @Test
    void shouldStayAnonymous_whenHeadersAreAbsent() throws Exception {
        when(request.getHeader(anyString())).thenReturn(null);

        invokeFilter();

        assertThat(currentAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldStayAnonymous_whenRoleHeaderIsMissing() throws Exception {
        when(request.getHeader(GatewayAuthenticationFilter.USER_ID_HEADER)).thenReturn(USER_ID.toString());
        when(request.getHeader(GatewayAuthenticationFilter.USER_ROLE_HEADER)).thenReturn(null);

        invokeFilter();

        assertThat(currentAuthentication()).isNull();
    }

    @Test
    void shouldStayAnonymous_whenUserIdHeaderIsBlank() throws Exception {
        when(request.getHeader(GatewayAuthenticationFilter.USER_ID_HEADER)).thenReturn("   ");
        when(request.getHeader(GatewayAuthenticationFilter.USER_ROLE_HEADER)).thenReturn("ADMINISTRATOR");

        invokeFilter();

        assertThat(currentAuthentication()).isNull();
    }

    @Test
    void shouldStayAnonymous_whenUserIdIsNotAValidUuid() throws Exception {
        when(request.getHeader(GatewayAuthenticationFilter.USER_ID_HEADER)).thenReturn("nao-e-uuid");
        when(request.getHeader(GatewayAuthenticationFilter.USER_ROLE_HEADER)).thenReturn("ADMINISTRATOR");

        invokeFilter();

        assertThat(currentAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldContinueChain_evenWhenAuthenticationIsNotEstablished() throws Exception {
        when(request.getHeader(anyString())).thenReturn(null);

        invokeFilter();

        verify(chain, times(1)).doFilter(request, response);
    }
}

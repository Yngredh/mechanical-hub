package com.fiap.mechanical_hub.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    static final String USER_ID_HEADER = "x-user-id";
    static final String USER_ROLE_HEADER = "x-user-role";
    static final String USER_NAME_HEADER = "x-user-name";

    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userId = request.getHeader(USER_ID_HEADER);
        String role = request.getHeader(USER_ROLE_HEADER);

        if (isBlank(userId) || isBlank(role)) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID parsedId = parseUserId(userId);
        if (parsedId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        GatewayPrincipal principal = new GatewayPrincipal(
                parsedId,
                request.getHeader(USER_NAME_HEADER),
                role
        );

        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private UUID parseUserId(String rawId) {
        try {
            return UUID.fromString(rawId);
        } catch (IllegalArgumentException ex) {
            log.warn("Cabecalho {} com valor invalido; requisicao seguira anonima", USER_ID_HEADER);
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

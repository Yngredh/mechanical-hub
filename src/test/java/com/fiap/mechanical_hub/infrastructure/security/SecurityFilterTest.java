package com.fiap.mechanical_hub.infrastructure.security;

import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.UserModelMock;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SecurityFilterTest {

    private final TokenService tokenService = mock(TokenService.class);
    private final UserJpaRepository userRepository = mock(UserJpaRepository.class);
    private final SecurityFilter filter = new SecurityFilter(tokenService, userRepository);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void invokeFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
        Method method = SecurityFilter.class.getDeclaredMethod("doFilterInternal",
                HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        method.invoke(filter, request, response, chain);
    }

    @Test
    void shouldContinueFilterChain_whenNoAuthorizationHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        invokeFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldContinueFilterChain_whenTokenIsInvalid() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(tokenService.validateToken("invalid-token")).thenReturn("");

        invokeFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldSetAuthentication_whenTokenIsValid() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(tokenService.validateToken("valid-token")).thenReturn("joao@email.com");
        when(userRepository.findByEmail("joao@email.com")).thenReturn(UserModelMock.active());

        invokeFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void shouldContinueFilterChain_whenUserNotFoundInDatabase() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(tokenService.validateToken("valid-token")).thenReturn("unknown@email.com");
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(null);

        invokeFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}

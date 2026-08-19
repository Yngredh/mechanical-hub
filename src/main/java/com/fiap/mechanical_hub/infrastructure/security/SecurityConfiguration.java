package com.fiap.mechanical_hub.infrastructure.security;

import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            GatewayAuthenticationFilter gatewayAuthenticationFilter) throws Exception {

        final String administrator = ProfileEnum.ADMINISTRATOR.name();
        final String mechanical = ProfileEnum.MECHANICAL.name();

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling ->
                        handling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/mechanical-hub/service-orders/**").permitAll()
                        .requestMatchers("/users", "/users/**").hasRole(administrator)
                        .requestMatchers("/customers/**").hasRole(administrator)
                        .requestMatchers("/vehicles/**").hasRole(administrator)
                        .requestMatchers("/services/**").hasRole(administrator)
                        .requestMatchers("/materials/**").hasRole(administrator)
                        .requestMatchers("/stock/**").hasRole(administrator)
                        .requestMatchers("/reports/**").hasRole(administrator)
                        .requestMatchers("/service-orders", "/service-orders/**")
                            .hasAnyRole(mechanical, administrator)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

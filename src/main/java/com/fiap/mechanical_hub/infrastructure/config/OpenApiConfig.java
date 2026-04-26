package com.fiap.mechanical_hub.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Mechanical Hub",
                version = "1.0",
                description = "Sistema de Gestão de Oficina Mecânica"
        )
)
public class OpenApiConfig {
}
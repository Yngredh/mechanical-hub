package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.authentication.AuthenticationRequest;
import com.fiap.mechanical_hub.application.dto.authentication.LoginResponse;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.infrastructure.security.TokenService;
import com.fiap.mechanical_hub.infrastructure.security.UserSecurityAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro de usuários")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    @Operation(
            summary = "Login de usuário",
            description = "Autentica um usuário com email e senha. Retorna um token JWT válido por 2 horas.",
            tags = {"Autenticação"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token gerado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<LoginResponse> login(@RequestBody AuthenticationRequest request) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(
                request.login(), 
                request.password()
        );

        var auth = this.authenticationManager.authenticate(usernamePassword);

        UserSecurityAdapter userDetails = (UserSecurityAdapter) auth.getPrincipal();
        User user = userDetails.user();
        
        var token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token));
    }

}

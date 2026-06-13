package com.fiap.mechanical_hub.infrastructure.http.exception;

import com.fiap.mechanical_hub.domain.exceptions.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnUnprocessableEntity_whenInvalidDocument() {
        InvalidDocumentException ex = new InvalidDocumentException("CPF inválido");
        ResponseEntity<Object> response = handler.handleInvalidDocument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        @SuppressWarnings("unchecked") Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 422);
        assertThat(body).containsEntry("message", "CPF inválido");
    }

    @Test
    void shouldReturnUnprocessableEntity_whenInvalidTelephone() {
        InvalidTelephoneException ex = new InvalidTelephoneException("Telefone inválido");
        ResponseEntity<Object> response = handler.handleInvalidTelephone(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        @SuppressWarnings("unchecked") Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 422);
        assertThat(body).containsEntry("message", "Telefone inválido");
    }

    @Test
    void shouldReturnConflict_whenDuplicatedDocument() {
        DuplicatedDocumentException ex = new DuplicatedDocumentException("Documento duplicado");
        ResponseEntity<Object> response = handler.handleDuplicateDocument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        @SuppressWarnings("unchecked") Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 409);
        assertThat(body).containsEntry("message", "Documento duplicado");
    }

    @Test
    void shouldReturnConflict_whenDuplicateLicensePlate() {
        DuplicateLicensePlateException ex = new DuplicateLicensePlateException("Placa duplicada");
        ResponseEntity<Object> response = handler.handleDuplicateLicensePlate(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        @SuppressWarnings("unchecked") Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 409);
        assertThat(body).containsEntry("message", "Placa duplicada");
    }

    @Test
    void shouldReturnUnprocessableEntity_whenInvalidLicensePlate() {
        InvalidLicensePlateException ex = new InvalidLicensePlateException("Placa inválida");
        ResponseEntity<Object> response = handler.handleInvalidLicensePlate(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        @SuppressWarnings("unchecked") Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 422);
        assertThat(body).containsEntry("message", "Placa inválida");
    }

    @Test
    void shouldReturnUnprocessableEntity_whenBusinessRuleException() {
        BusinessRuleException ex = new BusinessRuleException("Regra de negócio violada");
        ResponseEntity<Object> response = handler.handleBusinessRuleException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        @SuppressWarnings("unchecked") Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 422);
        assertThat(body).containsEntry("message", "Regra de negócio violada");
    }

    @Test
    void shouldReturnNotFound_whenNotFoundException() {
        NotFoundException ex = new NotFoundException("Recurso não encontrado");
        ResponseEntity<Object> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        @SuppressWarnings("unchecked") Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 404);
        assertThat(body).containsEntry("message", "Recurso não encontrado");
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");
        ResponseEntity<Object> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked") Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 400);
        assertThat(body).containsEntry("message", "Argumento inválido");
    }

    @Test
    void shouldReturnBadRequest_whenNoSuchElementException() {
        NoSuchElementException ex = new NoSuchElementException("Elemento não encontrado");
        ResponseEntity<Object> response = handler.handleNoSuchElement(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked") Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 400);
        assertThat(body).containsEntry("message", "Elemento não encontrado");
    }

    @Test
    void shouldReturnBadRequest_whenInvalidOrderTransitionException() {
        InvalidOrderTransitionException ex = new InvalidOrderTransitionException("Transição inválida");
        ResponseEntity<Object> response = handler.handleInvalidOrderTransition(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked") Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 400);
        assertThat(body).containsEntry("message", "Transição inválida");
    }
}

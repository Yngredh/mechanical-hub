package com.fiap.mechanical_hub.infrastructure.http.exception;

import com.fiap.mechanical_hub.domain.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    @ExceptionHandler(InvalidDocumentException.class)
    public ResponseEntity<Object> handleInvalidDocument(InvalidDocumentException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS, HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put(ERROR, "Documento Inválido");
        body.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidTelephoneException.class)
    public ResponseEntity<Object> handleInvalidTelephone(InvalidTelephoneException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS, HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put(ERROR, "Telefone Inválido");
        body.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(DuplicateDocumentException.class)
    public ResponseEntity<Object> handleDuplicateDocument(DuplicateDocumentException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS, HttpStatus.CONFLICT.value());
        body.put(ERROR, "Documento duplicado");
        body.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateLicensePlateException.class)
    public ResponseEntity<Object> handleDuplicateLicensePlate(DuplicateLicensePlateException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS, HttpStatus.CONFLICT.value());
        body.put(ERROR, "Placa duplicada");
        body.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidLicensePlateException.class)
    public ResponseEntity<Object> handleInvalidLicensePlate(InvalidLicensePlateException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS, HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put(ERROR, "Placa inválida");
        body.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Object> handleBusinessRuleException(BusinessRuleException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS, HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put(ERROR, "Violação de Regra de Negócio");
        body.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS, HttpStatus.NOT_FOUND.value());
        body.put(ERROR, "Not Found");
        body.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR, "Bad Request");
        body.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOrderTransitionException.class)
    public ResponseEntity<Object> handleInvalidOrderTransition(InvalidOrderTransitionException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ERROR, "Bad Request");
        body.put(MESSAGE, ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
package com.crediya.model.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleInvalidStatusException(ValidationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", "ESTADO_INVALIDO");
        response.put("message", ex.getMessage());
        response.put("details", "Los estados válidos son: EN_PROCESO, APROBADO, RECHAZADO");

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleLoanTypeNotFoundException(ValidationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", "TIPO_PRESTAMO_NO_ENCONTRADO");
        response.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Detecta específicamente errores de enum (como "No enum constant")
        if (ex.getMessage() != null && ex.getMessage().contains("enum constant")) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", "ESTADO_INVALIDO");
            response.put("message", "Estado inválido proporcionado");
            response.put("details", "Los estados válidos son: EN_PROCESO, APROBADO, RECHAZADO");

            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
        }

        // Para otras IllegalArgumentException
        Map<String, Object> response = new HashMap<>();
        response.put("code", "SOLICITUD_INVALIDA");
        response.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }
}
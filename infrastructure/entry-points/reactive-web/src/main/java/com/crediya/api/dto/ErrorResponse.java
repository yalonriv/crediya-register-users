package com.crediya.api.dto;

public record ErrorResponse(String code, String message) {
    // Ejemplo: {"code": "CLIENTE_NO_EXISTE", "message": "El cliente no existe"}
}

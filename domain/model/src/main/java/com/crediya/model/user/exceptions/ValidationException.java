package com.crediya.model.user.exceptions;

public class ValidationException extends RuntimeException {
    private final String customMessage;

    public ValidationException(String message) {
        super(message);
        this.customMessage = message;
    }

    @Override
    public String getMessage() {
        return customMessage;
    }
}
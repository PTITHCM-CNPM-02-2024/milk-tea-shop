package com.mts.backend.shared.exception;

public class DomainException extends RuntimeException{
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainException(String message) {
        super(message);
    }

    public DomainException() {
    }
}

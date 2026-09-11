package com.seth.backend.exception;

/** Thrown when a request is well-formed but violates a domain rule (mirrors the PL/pgSQL RAISE EXCEPTION guards). */
public class BusinessRuleViolationException extends RuntimeException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
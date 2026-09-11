package com.seth.backend.exception;

/** Thrown by service layer before hitting a DB unique constraint, so we can fail fast with a clean message. */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
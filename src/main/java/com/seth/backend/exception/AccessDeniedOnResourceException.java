package com.seth.backend.exception;

/** 403 — authenticated, but not permitted to act on this specific resource (IDOR guard). */
public class AccessDeniedOnResourceException extends RuntimeException {
    public AccessDeniedOnResourceException(String message) {
        super(message);
    }
}
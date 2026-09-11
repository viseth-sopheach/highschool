package com.seth.backend.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central error translation layer. Every response body is a ProblemDetail
 * (RFC 7807) so API consumers get one consistent error shape regardless
 * of what went wrong.
 *
 * Deliberately generic on 500s: internal messages/stack traces never
 * reach the client, only the correlation-worthy fields (status, title,
 * timestamp). Full detail goes to the log.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---- Domain exceptions -------------------------------------------------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(),
                "https://api.seth.com/errors/not-found");
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException ex) {
        return problem(HttpStatus.CONFLICT, "Duplicate Resource", ex.getMessage(),
                "https://api.seth.com/errors/duplicate");
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleViolationException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Business Rule Violation", ex.getMessage(),
                "https://api.seth.com/errors/business-rule");
    }

    @ExceptionHandler(AccessDeniedOnResourceException.class)
    public ProblemDetail handleResourceAccessDenied(AccessDeniedOnResourceException ex) {
        log.warn("IDOR-guard rejection: {}", ex.getMessage());
        return problem(HttpStatus.FORBIDDEN, "Access Denied", ex.getMessage(),
                "https://api.seth.com/errors/forbidden");
    }

    // ---- Security ------------------------------------------------------------

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleSpringAccessDenied(AccessDeniedException ex) {
        return problem(HttpStatus.FORBIDDEN, "Access Denied",
                "You do not have permission to perform this action.",
                "https://api.seth.com/errors/forbidden");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        return problem(HttpStatus.UNAUTHORIZED, "Authentication Failed",
                "Invalid username or password.",
                "https://api.seth.com/errors/unauthorized");
    }

    // ---- Bean validation (@Valid on @RequestBody) -----------------------------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.merge(fe.getField(), fe.getDefaultMessage(), (a, b) -> a + "; " + b);
        }

        ProblemDetail body = problem(HttpStatus.BAD_REQUEST, "Validation Failed",
                "One or more fields are invalid.",
                "https://api.seth.com/errors/validation");
        body.setProperty("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Validation Failed", ex.getMessage(),
                "https://api.seth.com/errors/validation");
    }

    // ---- Persistence -----------------------------------------------------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        // Catches unique-constraint / FK / CHECK violations that slipped past
        // service-layer validation (race conditions, direct DB triggers like
        // trg_validate_score_max raising an exception that Postgres wraps).
        log.warn("Data integrity violation", ex);
        return problem(HttpStatus.CONFLICT, "Data Conflict",
                "The request conflicts with existing data.",
                "https://api.seth.com/errors/data-conflict");
    }

    // ---- Fallbacks ---------------------------------------------------------

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        ProblemDetail body = problem(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed",
                ex.getMessage(), "https://api.seth.com/errors/method-not-allowed");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, WebRequest request) {
        log.error("Unhandled exception on {}", request.getDescription(false), ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                "https://api.seth.com/errors/internal");
    }

    // ---- helper -------------------------------------------------------------

    private ProblemDetail problem(HttpStatus status, String title, String detail, String typeUri) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create(typeUri));
        pd.setProperty("timestamp", OffsetDateTime.now());
        return pd;
    }
}
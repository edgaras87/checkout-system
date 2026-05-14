package com.edge.checkout.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail handleBodyValidation(MethodArgumentNotValidException ex) {
    ProblemDetail pd = validationProblem("Request body validation failed");

    Map<String, String> errors = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .collect(Collectors.toMap(
        FieldError::getField,
        this::messageOf,
        (a, b) -> a,
        LinkedHashMap::new
      ));

    pd.setProperty("errors", errors);
    return pd;
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  ProblemDetail handleMethodValidation(HandlerMethodValidationException ex) {
    ProblemDetail pd = validationProblem("Request parameter validation failed");

    Map<String, String> errors = new LinkedHashMap<>();

    ex.getAllErrors().forEach(error -> {
      String key = error.getCodes() != null && error.getCodes().length > 0
        ? error.getCodes()[0]
        : "request";

      String message = error.getDefaultMessage() == null
        ? "invalid"
        : error.getDefaultMessage();

      errors.putIfAbsent(key, message);
    });

    pd.setProperty("errors", errors);
    return pd;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ProblemDetail handleMalformedJson(HttpMessageNotReadableException ex) {
    return problem(
      HttpStatus.BAD_REQUEST,
      "Malformed JSON",
      "Request body is not valid JSON"
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ProblemDetail handleBadRequest(IllegalArgumentException ex) {
    return problem(
      HttpStatus.BAD_REQUEST,
      "Bad request",
      ex.getMessage() == null ? "Invalid request" : ex.getMessage()
    );
  }

  @ExceptionHandler(IllegalStateException.class)
  ProblemDetail handleInvariantViolation(IllegalStateException ex) {
    // IllegalStateException represents server-side invariant violations.
    // These are never client errors.
    log.error("Invariant violated", ex);

    return problem(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "Invariant violated",
      "A server-side invariant was violated. See logs for details."
    );
  }

  private ProblemDetail validationProblem(String detail) {
    return problem(HttpStatus.BAD_REQUEST, "Validation failed", detail);
  }

  private ProblemDetail problem(HttpStatus status, String title, String detail) {
    ProblemDetail pd = ProblemDetail.forStatus(status);
    pd.setTitle(title);
    pd.setDetail(detail);
    return pd;
  }

  private String messageOf(FieldError fieldError) {
    return fieldError.getDefaultMessage() == null
      ? "invalid"
      : fieldError.getDefaultMessage();
  }
}

package org.havoc.skilltrack.exception;

import lombok.extern.slf4j.Slf4j;
import org.havoc.skilltrack.exception.CustomException.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //  Handles validation failures (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles custom + generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        StackTraceElement trace = ex.getStackTrace()[0];

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof EmailAlreadyExistsException ||
                ex instanceof InvalidCredentialsException ||
                ex instanceof EmailDoesNotExistsException ||
                ex instanceof UsernameAlreadyExistsException ||
                ex instanceof UserNotFoundException
        ) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof IllegalStateException) {
            status = HttpStatus.CONFLICT;
        }

        log.error("Exception: {} at {}.{}({}:{})",
                ex.getMessage(),
                trace.getClassName(),
                trace.getMethodName(),
                trace.getFileName(),
                trace.getLineNumber()
        );

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", ex.getMessage());
        response.put("fileName", trace.getFileName());
        response.put("lineNumber", trace.getLineNumber());
        response.put("methodName", trace.getClassName() + "." + trace.getMethodName());

        return new ResponseEntity<>(response, status);
    }
}

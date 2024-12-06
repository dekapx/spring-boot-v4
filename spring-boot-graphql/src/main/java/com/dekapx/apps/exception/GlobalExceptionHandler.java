package com.dekapx.apps.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleDefaultError(Exception e) {
        final ErrorMessage errorMessage = new ErrorMessage(INTERNAL_SERVER_ERROR.value(), e.getMessage());
        log.error("Unexpected error...", e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException e) {
        final ErrorMessage errorMessage = new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
        log.error("Unexpected error...", e);
        return ResponseEntity.status(BAD_REQUEST).body(errorMessage);
    }
}

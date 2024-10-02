package com.dekapx.apps.aspect;

import com.dekapx.apps.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice(annotations = RestController.class)
public class RestControllerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        final ApiErrorResponse apiErrorResponse = apiErrorFunction.apply(exception.getMessage(), "Bad Request");
        return new ResponseEntity<>(apiErrorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException exception) {
        final ApiErrorResponse apiErrorResponse = apiErrorFunction.apply(exception.getMessage(), "Not Found");
        return new ResponseEntity<>(apiErrorResponse, NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ApiErrorResponse>> handleValidations(MethodArgumentNotValidException exception) {
        final List<ApiErrorResponse> errors = new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            errors.add(apiErrorFunction.apply(error.getDefaultMessage(), "Bad Request"));
        });
        return new ResponseEntity<>(errors, BAD_REQUEST);
    }

    private BiFunction<String, String, ApiErrorResponse> apiErrorFunction = (errorMessage, status) ->
            new ApiErrorResponse(errorMessage, status, LocalDateTime.now());
}

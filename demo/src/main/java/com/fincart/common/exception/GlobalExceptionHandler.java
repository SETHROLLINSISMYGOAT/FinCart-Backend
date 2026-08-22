package com.fincart.common.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex) {

        ErrorResponse response =
                new ErrorResponse(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex) {
        ErrorResponse error =
                new ErrorResponse(

                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }


    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex) {

        ErrorResponse response =
                new ErrorResponse(
                        ex.getMessage(),
                        HttpStatus.CONFLICT.value()
                                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField()
                                + ": "
                                + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse response =
                new ErrorResponse(
                        message,
                        HttpStatus.BAD_REQUEST.value()
                                );

        return ResponseEntity
                .badRequest()
                .body(response);
    }
}
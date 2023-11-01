package com.ioliveira.admin.catalogo.infrastructure.api.controllers;

import com.ioliveira.admin.catalogo.domain.exceptions.DomainException;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.admin.catalogo.domain.validation.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DomainException.class)
    public ResponseEntity<?> handleDomainException(final DomainException exception) {
        return ResponseEntity
                .unprocessableEntity()
                .body(ApiError.from(exception));
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(final NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.from(exception));
    }

    record ApiError(String message, List<Error> errors) {
        public static ApiError from(final DomainException exception) {
            return new ApiError(exception.getMessage(), exception.getErrors());
        }
    }
}

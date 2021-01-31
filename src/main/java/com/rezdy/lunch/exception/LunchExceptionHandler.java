package com.rezdy.lunch.exception;

import com.rezdy.lunch.exception.model.ErrorResponse;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class LunchExceptionHandler {

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDateException() {
        return new ResponseEntity<>(new ErrorResponse().setErrorMessage("Invalid Request: Parameters given aren't given correctly"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleNoResultException() {
        return new ResponseEntity<>(new ErrorResponse().setErrorMessage("No result(s) found for request"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(DateTimeParseException exception) {
        return new ResponseEntity<>(new ErrorResponse().setErrorMessage("Request was unable to be processed"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

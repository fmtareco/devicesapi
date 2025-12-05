package com.example.devicesapi.controllers;


import com.example.devicesapi.exceptions.InvalidFieldValueException;
import com.example.devicesapi.exceptions.InvalidOperationException;
import com.example.devicesapi.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * handles all exceptions caused by missing or invalid input values
     *
     * @param ex - throwed exception
     * @return
     */
    @ExceptionHandler(InvalidFieldValueException.class)
    public ResponseEntity<String> handleInvalidValues(InvalidFieldValueException ex) {
        return new ResponseEntity<>("Invalid values: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * handles all exceptions caused by attempts to execute operations
     * forbidden due to the resources state
     *
     * @param ex - throwed exception
     * @return
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<String> handleInvalidOperation(InvalidOperationException ex) {
        return new ResponseEntity<>("Invalid operation: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * handles all exceptions related with failed resource selection
     *
     * @param ex - throwed exception
     * @return
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>("Not found: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}

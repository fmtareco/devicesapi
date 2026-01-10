package com.example.devicesapi.controllers;


import com.example.devicesapi.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInfo> handleInvalidArguments(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));
        return getErrorResponse(ex, request, HttpStatus.BAD_REQUEST, errors);
    }

    /**
     * handles all exceptions caused by invalid input values
     * @param ex throwed exception
     * @param request http request
     * @return Response w/ ErrorInfo
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorInfo> handleInvalidValues(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        return getErrorResponse(ex, request, HttpStatus.BAD_REQUEST, null);
    }

    /**
     * handles all exceptions caused by missing or invalid input values
     * @param ex throwed exception
     * @param request http request
     * @return Response w/ ErrorInfo
     */
    @ExceptionHandler(InvalidDuplicatedValuesException.class)
    public ResponseEntity<ErrorInfo> handleDuplicatedValues(
            InvalidFieldValueException ex,
            HttpServletRequest request) {
        return getErrorResponse(ex, request, HttpStatus.CONFLICT, null);
    }

    /**
     * handles all exceptions caused by missing or invalid input values
     * @param ex throwed exception
     * @param request http request
     * @return Response w/ ErrorInfo
     */
    @ExceptionHandler(InvalidFieldValueException.class)
    public ResponseEntity<ErrorInfo> handleInvalidValues(
            InvalidFieldValueException ex,
            HttpServletRequest request) {
        return getErrorResponse(ex, request, HttpStatus.BAD_REQUEST, null);
    }

    /**
     * handles all exceptions caused by attempts to execute operations
     * forbidden due to the resources state
     *
     * @param ex throwed exception
     * @param request http request
     * @return Response w/ ErrorInfo
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorInfo> handleInvalidOperation(
            InvalidOperationException ex,
            HttpServletRequest request) {
        return getErrorResponse(ex, request, HttpStatus.CONFLICT, null);
    }

    /**
     * handles all exceptions related with failed resource selection
     *
     * @param ex throwed exception
     * @param request http request
     * @return Response w/ ErrorInfo
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        return getErrorResponse(ex, request, HttpStatus.NOT_FOUND, null);
    }

    /**
     * formats an error response
     * @param ex throwed exception
     * @param request http request
     * @param status http status code
     * @return Response w/ ErrorInfo
     */
    public ResponseEntity<ErrorInfo> getErrorResponse(
            Exception ex,
            HttpServletRequest request,
            HttpStatus status,
            Map<String, String> errors) {
        ErrorInfo info = getErrorInfo(ex, request, status, errors);
        return new ResponseEntity<>(info, status);
    }

    /**
     * return a record with the error relevant information
     * @param ex throwed exception
     * @param request http request
     * @param status http status code
     * @return error info structure
     */
    public ErrorInfo getErrorInfo(Exception ex, HttpServletRequest request, HttpStatus status, Map<String, String> errors)
    {
        return new ErrorInfo(ex.getMessage(),
                status.getReasonPhrase(),
                status.value(),
                request.getRequestURI(),
                Instant.now(),
                errors
        );
    }
}

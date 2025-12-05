package com.example.devicesapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidFieldValueException extends RuntimeException {
    public InvalidFieldValueException(String field, String value) {
        super("Invalid value("+value+") for field[" + field + "]");
    }
    public InvalidFieldValueException(String message) {
        super(message);
    }
}
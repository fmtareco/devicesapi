package com.example.devicesapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidNullValueException extends InvalidFieldValueException {
    public InvalidNullValueException(String field) {
        super("Field[" + field + "] cannot be null or empty");
    }
}
package com.example.devicesapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class InvalidUpdateOnLockException extends InvalidOperationException {
    public InvalidUpdateOnLockException(String field) {
        super("Cannot change " + field+ " while device is in use");
    }
}
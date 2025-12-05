package com.example.devicesapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class InvalidDeleteException extends InvalidOperationException {
    public InvalidDeleteException(UUID id) {
        super("Cannot delete device("+id+") while it is in use");
    }
}
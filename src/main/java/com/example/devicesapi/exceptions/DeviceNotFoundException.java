package com.example.devicesapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DeviceNotFoundException extends ResourceNotFoundException {
    public DeviceNotFoundException(UUID id) {
        super("Device("+id+") not found");
    }
}
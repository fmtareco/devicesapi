package com.example.devicesapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidDuplicatedValuesException extends InvalidFieldValueException {
    public InvalidDuplicatedValuesException(String[] fields, String[] values) {
        super("Duplicated value set {"+ Arrays.toString(values)+"} for fields {"+Arrays.toString(fields)+"}");
    }
}
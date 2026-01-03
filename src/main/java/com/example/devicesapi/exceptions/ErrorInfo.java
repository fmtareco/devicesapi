package com.example.devicesapi.exceptions;

import java.time.Instant;
import java.util.Map;

public record ErrorInfo(
        String message,
        String error,
        int statusCode,
        String path,
        Instant timestamp,
        Map<String, String> fieldErrors) {
}

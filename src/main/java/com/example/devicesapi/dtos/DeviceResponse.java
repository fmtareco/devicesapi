package com.example.devicesapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO Pattern (Data Transfer Object)
 * Passing all output data to client
 */
@Builder
public record DeviceResponse (
    UUID id,
    String name,
    String brand,
    String state,
    OffsetDateTime createdAt
){}
package com.example.devicesapi.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO Pattern (Data Transfer Object)
 * Passing all output data to client
 */
@Getter
@Setter
public class DeviceResponse {
    private UUID id;
    private String name;
    private String brand;
    private String state;
    private OffsetDateTime createdAt;
}
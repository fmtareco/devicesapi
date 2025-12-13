package com.example.devicesapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


/**
 * DTO Pattern (Data Transfer Object)
 * Passing public data to the devices creation process
 * - id and createdAt intentionally omitted (internally set)
 */
@Builder
public record DeviceCreateRequest(
    @NotBlank String name,
    @NotBlank String brand,
    @NotNull String state) // "AVAILABLE", "IN_USE", "INACTIVE"
    {}
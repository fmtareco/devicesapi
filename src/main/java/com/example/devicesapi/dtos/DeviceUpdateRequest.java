package com.example.devicesapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * DTO Pattern (Data Transfer Object)
 * Passing public data to the devices update processes
 * - id and createdAt intentionally omitted (cannot be updated)
 * - all others fields nullable (to allow partial updates)
 */
@Builder
public record DeviceUpdateRequest(
        @NotBlank(message = "Device name is required")
        @Size(min = 3, max = 50, message = "Device name must be 3-50 characters")
        String name,
        @NotBlank(message = "Brand is required")
        @Size(min = 3, max = 50, message = "Brand must be 3-50 characters")
        String brand,
        @NotBlank(message = "State is required")
        @Size(min = 6, max = 9, message = "State valid values: \"AVAILABLE\", \"IN_USE\", \"INACTIVE\"")
        String state
){}
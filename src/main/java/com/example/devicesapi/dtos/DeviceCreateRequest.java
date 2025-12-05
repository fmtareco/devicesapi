package com.example.devicesapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 * DTO Pattern (Data Transfer Object)
 * Passing public data to the devices creation process
 * - id and createdAt intentionally omitted (internally set)
 */
@Getter
@Setter
@Builder
public class DeviceCreateRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String brand;
    @NotNull
    private String state; // "AVAILABLE", "IN_USE", "INACTIVE"
}
package com.example.devicesapi.dtos;

import lombok.Builder;

/**
 * DTO Pattern (Data Transfer Object)
 * Passing public data to the devices update processes
 * - id and createdAt intentionally omitted (cannot be updated)
 * - all others fields nullable (to allow partial updates)
 */
@Builder
public record DeviceUpdateRequest(
    String name,
    String brand,
    String state
){}
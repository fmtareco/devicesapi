package com.example.devicesapi.dtos;

import lombok.Builder;
import lombok.Data;


/**
 * DTO Pattern (Data Transfer Object)
 * Passing public data to the devices update processes
 * - id and createdAt intentionally omitted (cannot be updated)
 * - all others fields nullable (to allow partial updates)
 */
@Builder
@Data
public class DeviceUpdateRequest {
    private String name;
    private String brand;
    private String state;
}
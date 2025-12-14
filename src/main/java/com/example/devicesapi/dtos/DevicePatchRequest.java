package com.example.devicesapi.dtos;

import lombok.Builder;

import java.util.Optional;


/**
 * DTO Pattern (Data Transfer Object)
 * Passing public data to the devices update processes
 * - id and createdAt intentionally omitted (cannot be updated)
 * - all others fields nullable (to allow partial updates)
 */
@Builder
public record DevicePatchRequest(
    Optional<String> name,
    Optional<String> brand,
    Optional<String> state
){}
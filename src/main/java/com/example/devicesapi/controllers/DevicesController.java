package com.example.devicesapi.controllers;

import com.example.devicesapi.dtos.DeviceCreateRequest;
import com.example.devicesapi.dtos.DeviceResponse;
import com.example.devicesapi.dtos.DeviceUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
public class DevicesController {

    /**
     * POST - Creates a new device
     * @return DeviceResponse with the new device content
     */
    @PostMapping
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceCreateRequest req) {
        var created = DeviceResponse.builder()
                .id(UUID.randomUUID())
                .name("dummy device")
                .brand("dummy brand")
                .state("AVAILABLE")
                .build();
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * PUT {id} - Fully update an existing device.
     * @param id - identifies the device to be updated
     * @param req - DeviceUpdateRequest instance with the new data
     * @return DeviceResponse with the update device content
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> replace(@PathVariable UUID id, @Valid @RequestBody DeviceUpdateRequest req) {
        var updated = DeviceResponse.builder()
                .id(UUID.randomUUID())
                .name("dummy device alt")
                .brand("dummy brand alt")
                .state("IN_USE")
                .build();
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH {id} - Partially updates an existing device.
     * @param id - identifies the device to be updated
     * @param req - DeviceUpdateRequest instance with the new data
     * @return DeviceResponse with the update device content
     */
    @PatchMapping("/{id}")
    public ResponseEntity<DeviceResponse> partialUpdate(@PathVariable UUID id, @RequestBody DeviceUpdateRequest req) {
        var updated = DeviceResponse.builder()
                .id(UUID.randomUUID())
                .name("dummy device alt")
                .brand("dummy brand alt")
                .state("IN_USE")
                .build();
        return ResponseEntity.ok(updated);
    }

    /**
     * GET {id} - Fetches a single device w/ id
     * @param id - identifies the device to be fetched
     * @return DeviceResponse with the fetched device content
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getOne(@PathVariable UUID id) {
        var fetched = DeviceResponse.builder()
                .id(UUID.randomUUID())
                .name("dummy device selected")
                .brand("dummy brand selected")
                .state("INACTIVE")
                .build();
        return ResponseEntity.ok(fetched);
    }

    /**
     * GET  - Fetches all devices, with or without brand or state filter
     * @param brand - optional brand value to filter the list
     * @param state - optional state value to filter the list
     * @return list of DeviceResponse corresponding to the matching devices
     */
    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAll(@RequestParam(required = false) String brand, @RequestParam(required = false) String state) {
        List<DeviceResponse> devicesList = new ArrayList<>();
        DeviceResponse dr;
        for (int i = 0; i < 5 ; i++) {
            dr = DeviceResponse.builder()
                    .id(UUID.randomUUID())
                    .name("device %d".formatted(i))
                    .brand("brand %d".formatted(i))
                    .state("AVAILABLE")
                    .build();
            devicesList.add(dr);
        }
        return ResponseEntity.ok(devicesList);
    }

    /**
     * DELETE {id} - eliminates a particular device w/ id
     * @param id - identifies the device to be deleted
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
    }
}
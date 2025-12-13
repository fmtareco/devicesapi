package com.example.devicesapi.controllers;

import com.example.devicesapi.dtos.DeviceCreateRequest;
import com.example.devicesapi.dtos.DevicePatchRequest;
import com.example.devicesapi.dtos.DeviceResponse;
import com.example.devicesapi.dtos.DeviceUpdateRequest;
import com.example.devicesapi.services.DevicesService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
public class DevicesController {

    /**
     * Dependency injection of the services
     *
     */
    private final DevicesService svc;

    public DevicesController(DevicesService svc) {
        this.svc = svc;
    }


    /**
     * POST - Creates a new device
     * @return DeviceResponse with the new device content
     */
    @PostMapping
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceCreateRequest req) {
        var created = svc.create(req);
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
        var updated = svc.update(id, req);
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH {id} - Partially updates an existing device.
     * @param id - identifies the device to be updated
     * @param req - DeviceUpdateRequest instance with the new data
     * @return DeviceResponse with the update device content
     */
    @PatchMapping("/{id}")
    public ResponseEntity<DeviceResponse> partialUpdate(@PathVariable UUID id, @RequestBody DevicePatchRequest req) {
        var updated = svc.partialUpdate(id, req);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET {id} - Fetches a single device w/ id
     * @param id - identifies the device to be fetched
     * @return DeviceResponse with the fetched device content
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(svc.getOne(id));
    }

    /**
     * GET  - Fetches all devices, with or without brand or state filter
     * @param brand - optional brand value to filter the list
     * @param state - optional state value to filter the list
     * @return list of DeviceResponse corresponding to the matching devices
     */
    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String state,
            @RequestParam(value = "page", defaultValue = "0" ) int page,
            @RequestParam(value = "size", defaultValue = "10" ) int size,
            @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = getSort(ascending);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(
                svc.getAll(
                        Optional.ofNullable(name),
                        Optional.ofNullable(brand),
                        Optional.ofNullable(state),
                        pageable));
    }

    /**
     * return the Sort criteria
     * @param ascending - indicates the input sort direction
     * @return Sort criteria
     */
    private Sort getSort(boolean ascending) {
        Sort.Direction direction = ascending ?
                Sort.Direction.ASC:
                Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        Sort.Order brandOrder = new Sort.Order(direction, "brand");
        orders.add(brandOrder);
        Sort.Order nameOrder = new Sort.Order(direction, "name");
        orders.add(nameOrder);
        return Sort.by(orders);
    }

    /**
     * DELETE {id} - eliminates a particular device w/ id
     * @param id - identifies the device to be deleted
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        svc.delete(id);
    }
}
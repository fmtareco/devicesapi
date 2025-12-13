package com.example.devicesapi.services;


import com.example.devicesapi.annotations.TrackExecution;
import com.example.devicesapi.dtos.DeviceCreateRequest;
import com.example.devicesapi.dtos.DevicePatchRequest;
import com.example.devicesapi.dtos.DeviceResponse;
import com.example.devicesapi.dtos.DeviceUpdateRequest;
import com.example.devicesapi.entities.Device;
import com.example.devicesapi.entities.Device.State;
import com.example.devicesapi.exceptions.DeviceNotFoundException;
import com.example.devicesapi.exceptions.InvalidDeleteException;
import com.example.devicesapi.exceptions.InvalidDuplicatedValuesException;
import com.example.devicesapi.exceptions.InvalidNullValueException;
import com.example.devicesapi.repository.DevicesRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.devicesapi.repository.DevicesRepository.*;

@Service
public class DevicesService {

    /**
     * Dependency injection of the repository
     *
     */
    private final DevicesRepository repo;

    public DevicesService(DevicesRepository repo) {
        this.repo = repo;
    }

    //---------------------------------------------------------------------------------------//
    //                              public life cycle methods                                //
    //---------------------------------------------------------------------------------------//

    /**
     * Creates a new Device, based on the values of the input DeviceCreateRequest
     * first it validates if the State value is valid and
     * if the device identification (name+brand) is not null and unique
     * if it is ok, creates a new device providing a random uuid and the current date
     * At the end, saves the Device on the db, through the repo
     *
     * @param req - DataCreateRequest with the provided values for the new device
     * @return DeviceResponse with the new Device content
     */
    @Transactional
    @TrackExecution
    public DeviceResponse create(DeviceCreateRequest req) {
        validateIdentification(req.name(),req.brand());
        State state = Device.State.from(req.state());
        Device device = repo.save(
                Device.create(req.name(), req.brand(), state));
        return toDto(device);
    }

    /**
     * Fully Updates an existent device
     * At the end, saves the Device on the db, through the repo
     *
     * @param id - id of the device to be updated
     * @param req - DeviceUpdateRequest with the provided values for the update
     * @return DeviceResponse with the updated Device content
     */
    @TrackExecution
    public DeviceResponse update(UUID id, DeviceUpdateRequest req) {
        var device = findDevice(id);
        validateDuplicates(device, req.name(), req.brand());
        device.updateName(req.name());
        device.updateBrand(req.brand());
        device.updateState(req.state());
        return toDto(repo.save(device));
    }

    /**
     * Partially updates an existent device
     * At the end, saves the Device on the db, through the repo
     *
     * @param id - id of the device to be updated
     * @param req - DeviceUpdateRequest with the provided values for the update
     * @return DeviceResponse with the updated Device content
     */
    @TrackExecution
    public DeviceResponse partialUpdate(UUID id, DevicePatchRequest req) {
        var device = findDevice(id);
        req.name()
            .filter(name -> !name.equals(device.getName()))
            .ifPresent(name -> {
                validateDuplicates(device, name, device.getBrand());
                device.updateName(name);
            });
        req.brand()
            .filter(brand -> !brand.equals(device.getBrand()))
            .ifPresent(brand -> {
                validateDuplicates(device, device.getName(), brand);
                device.updateBrand(brand);
            });
        req.state()
                .map(Device.State::from)
                .ifPresent(device::updateState);
        return toDto(repo.save(device));
    }

    /**
     * fetches an existent device, from the input
     * It locates the device through the id and
     * if found, returns its content
     *
     * @param id - id of the device to be updated
     * @return DeviceResponse with the selected Device content
     */
    @Transactional(readOnly = true)
    @TrackExecution
    public DeviceResponse getOne(UUID id) {
        Device device = findDevice(id);
        return toDto(device);
    }

    /**
     * fetches a list of all existent device,optionally filtered by brand or state
     * If it requires a filter, delegates on the method for that filter
     *
     * @param name  - when present, indicates that only devices of that name should be returned
     * @param brand - when present, indicates that only devices of that brand should be returned
     * @param state - when present, indicates that only devices on that state should be returned
     * @param pageable - provides info about the pagination
     * @return list of DeviceResponse corresponding to the selected Devices
     */
    @Transactional(readOnly = true)
    @TrackExecution
    public List<DeviceResponse> getAll(Optional<String> name, Optional<String> brand, Optional<String> state, Pageable pageable) {
        return repo.findAll(byFilters(name,brand,state), pageable)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * fetches an existent device, from the input it
     * First It locates the device and checks if it can be deleted (not on Lock state)
     * @param id - id of the device to be updated
     */
    @Transactional
    @TrackExecution
    public void delete(UUID id) {
        var device = findDevice(id);
        if (device.isLocked()) {
            throw new InvalidDeleteException(id);
        }
        repo.delete(device);
    }

    //---------------------------------------------------------------------------------------//
    //                              internal utility methods                                 //
    //---------------------------------------------------------------------------------------//

    /**
     * Locates a device with the arg Id
     * @param id - id of the device to find
     * @return the located device
     */
    private Device findDevice(UUID id) {
        return repo.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
    }

    /**
     * to convert the created/updated/selected Device to a
     * DeviceResponse to return to the API caller
     * @param device - the device to be converted
     * @return DeviceResponse
     */
    private DeviceResponse toDto(Device device) {
        return DeviceResponse.builder()
            .id(device.getId())
            .brand(device.getBrand())
                .name(device.getName())
                .state(device.getState().name())
                .createdAt(device.getCreatedAt())
                .build();
    }


    /**
     * Checks if the identification values (name, brand) are valid (not null)
     * and that there isn't already a device with that identification
     * @param name - name of the device
     * @param brand - brand of the device
     */
    private void validateIdentification(String name, String brand) {
        if (name == null || name.isBlank())
            throw new InvalidNullValueException("name");
        if (brand == null || brand.isBlank())
            throw new InvalidNullValueException("brand");
        validateDuplicates(null, name, brand);
    }

    /**
     * Checks for device duplicates, by locating an existing device with the input name and brand
     * @param dev - device to be updated
     * @param name - name of the device to be located
     * @param brand - brand of the device to be located
     * @throws InvalidDuplicatedValuesException, if it exists a duplicate
     */
    private void validateDuplicates(Device dev, String name, String brand) {
        repo.findDeviceByNameAndBrand(name, brand).stream()
                .filter(existing -> !existing.equals(dev))
                .findAny()
                .ifPresent(d -> {
                    throw new InvalidDuplicatedValuesException(
                            new String[]{"name", "brand"},
                            new String[]{name, brand}
                    );
                });
    }

}

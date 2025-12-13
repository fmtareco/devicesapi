package com.example.devicesapi.services;


import com.example.devicesapi.annotations.TrackExecution;
import com.example.devicesapi.dtos.DeviceCreateRequest;
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
import org.springframework.util.ObjectUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.devicesapi.repository.DevicesRepository.Specs.*;

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
        State stateValue = Device.getDeviceStateValue(req.getState());
        checkDeviceIdentification(req.getName(),req.getBrand());
        Device device = repo.save(
                Device.createDevice(req.getName(), req.getBrand(), stateValue));
        return toDto(device);
    }

    /**
     * Updates an existent device, either partially or fully
     * First, it locates the device through the id
     * If found, it validates all input values, depending on the update mode (partial or full)
     * - on full mode, all field values are required:
     *      checks for null or duplicates on id
     *      checks if the provided state is valid
     *  - on partial mode, ignores null values
     * At the end, saves the Device on the db, through the repo
     *
     * @param id - id of the device to be updated
     * @param req - DeviceUpdateRequest with the provided values for the update
     * @param partial - indicates whether the update is partial or full
     * @return DeviceResponse with the updated Device content
     */
    @Transactional
    @TrackExecution
    public DeviceResponse update(UUID id, DeviceUpdateRequest req, boolean partial) {
        Device device = repo.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        String newName = req.getName();
        String newBrand = req.getBrand();
        boolean reqNewName = !ObjectUtils.isEmpty(newName)&&!device.getName().equals(newName);
        boolean reqNewBrand = !ObjectUtils.isEmpty(newBrand)&&!device.getBrand().equals(req.getBrand());
        if (reqNewName||reqNewBrand) {
            String ckName = newName, ckBrand = newBrand;
            if (partial) {
                ckName=!ObjectUtils.isEmpty(newName)?newName:device.getName();
                ckBrand=!ObjectUtils.isEmpty(newBrand)?newBrand:device.getBrand();
            }
            checkDeviceDuplicates(device, ckName,ckBrand);
        }
        if (!partial || reqNewName)
            device.updateName(newName);
        if (!partial || reqNewBrand)
            device.updateBrand(newBrand);
        String newState = req.getState();
        if (!partial || !ObjectUtils.isEmpty(newState))
            device.updateState(newState);
        Device saved = repo.save(device);
        return toDto(saved);
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
        Device device = repo.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        return toDto(device);
    }

    /**
     * fetches a list of all existent device,optionally filtered by brand or state
     * If it requires a filter, delegates on the method for that filter
     *
     * @param brand    - when present, indicates that only devices of that brand should be returned
     * @param stateStr - when present, indicates that only devices on that state should be returned
     * @param pageable - provides info about the pagination
     * @return list of DeviceResponse corresponding to the selected Devices
     */
    @Transactional(readOnly = true)
    @TrackExecution
    public List<DeviceResponse> getAll(String name, String brand, String stateStr, Pageable pageable) {
        State state = stateStr != null?
                Device.getDeviceStateValue(stateStr) : State.AVAILABLE;
        if (name != null) {
            return repo.findAll(byFlexibleSearch(name, brand,state),pageable).stream().map(this::toDto).collect(Collectors.toList());
        }
        if (brand != null && stateStr != null) {
            return repo.findAll(byBrandAndState(brand,state),pageable).stream().map(this::toDto).collect(Collectors.toList());
        }
        if (brand != null) {
            return repo.findAll(byBrand(brand),pageable).stream().map(this::toDto).collect(Collectors.toList());
        }
        if (stateStr != null) {
            return repo.findAll(byState(state),pageable).stream().map(this::toDto).collect(Collectors.toList());
        }
        return repo.findAll(pageable).stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * fetches an existent device, from the input it
     * First It locates the device and checks if it can be deleted (not on Lock state)
     * @param id - id of the device to be updated
     */
    @Transactional
    @TrackExecution
    public void delete(UUID id) {
        Device device = repo.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        if (device.isLocked()) {
            throw new InvalidDeleteException(id);
        }
        repo.delete(device);
    }




    //---------------------------------------------------------------------------------------//
    //                              internal utility methods                                 //
    //---------------------------------------------------------------------------------------//

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
     * locates a device with an identification matching the name+brand from the input
     * @param name - name of the device to be located
     * @param brand - brand of the device to be located
     * @return the located device or null
     */
    private Device locateDevice(String name, String brand) {
        return repo.findDeviceByNameAndBrand(name, brand).stream()
                .findAny()
                .orElse(null);
//        return repo.findAll().stream()
//                .filter(d-> d.getName().equals(name) && d.getBrand().equals(brand))
//                .findAny()
//                .orElse(null);
    }

    /**
     * Checks if the identification values (name, brand) are valid (not null)
     * and that there isn't already a device with that identification
     * @param name - name of the device
     * @param brand - brand of the device
     */
    private void checkDeviceIdentification(String name, String brand) {
        if (ObjectUtils.isEmpty(name)) {
            throw new InvalidNullValueException("name");
        }
        if (ObjectUtils.isEmpty(brand)) {
            throw new InvalidNullValueException("brand");
        }
        checkDeviceDuplicates(null, name, brand);
    }

    /**
     * Checks for device duplicates, by locating an existing device with the input name and brand
     * @param dev - device to be updated
     * @param name - name of the device to be located
     * @param brand - brand of the device to be located
     * @throws InvalidDuplicatedValuesException, if it exists a duplicate
     */
    private void checkDeviceDuplicates(Device dev, String name, String brand) {
        Device oldDev = locateDevice(name, brand);
        if (oldDev == null)
            return;
        if (oldDev!=dev) {
            throw new InvalidDuplicatedValuesException(new String[]{"name", "brand"}, new String[]{name, brand});
        }
    }

}

package com.example.devicesapi.repository;


import com.example.devicesapi.entities.Device;
import com.example.devicesapi.entities.Device.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DevicesRepository extends JpaRepository<Device, UUID> {
    List<Device> findByBrand(String brand);
    List<Device> findByState(State state);
}
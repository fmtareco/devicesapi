package com.example.devicesapi.repository;


import com.example.devicesapi.entities.Device;
import com.example.devicesapi.entities.Device.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository design pattern :
 * - provides a centralized way to manage all data operations
 * - hides data storage particularities, streamlining changes of the data provider
 *  */
public interface DevicesRepository extends JpaRepository<Device, UUID> {
    List<Device> findByBrand(String brand);
    List<Device> findByState(State state);
}
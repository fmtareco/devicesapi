package com.example.devicesapi.entities;

import com.example.devicesapi.exceptions.InvalidFieldValueException;
import com.example.devicesapi.exceptions.InvalidNullValueException;
import com.example.devicesapi.exceptions.InvalidUpdateOnLockException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "devices")
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    @Column(name = "creation_time", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public enum State {
        AVAILABLE,
        IN_USE,
        INACTIVE;
        public static State from(String _state) {
            try {
                return State.valueOf(_state);
            } catch (IllegalArgumentException e) {
                throw new InvalidFieldValueException("state", _state);
            }
        }


    }

    /**
     * determines the state where a device can't be updated or deleted
     * @return boolean stating that the device is locked
     */
    public boolean isLocked() {
        return (state == State.IN_USE);
    }

    /**
     * checks if a particular field can assume a given value
     * depends on the device lock state and on the value
      * @param field
     * @param value
     */
    private void checkUpdateAllowed(String field, String value) {
        if (value == null || value.isEmpty()) {
            throw new InvalidNullValueException(field);
        }
        if (isLocked()) {
            throw new InvalidUpdateOnLockException(field);
        }
    }

    /**
     * (tries to) update the device name
     * depends on the lock state and the new name value
     * @param _name : new name
     */
    public void updateName(String _name) {
        checkUpdateAllowed("name", _name);
        setName(_name);
    }

    /**
     * (tries to) update the device brand
     * depends on the lock state and the new brand value
     * @param _brand
     */
    public void updateBrand(String _brand) {
        checkUpdateAllowed("brand", _brand);
        setBrand(_brand);
    }

    /**
     * updates the state with a state string argument
     * if the state text is invalid, thows an exception
     * @param _state
     */
    public void updateState(String _state) {
        updateState(State.from(_state));
    }
    public void updateState(State _state) {
        setState(_state);
    }

    public static Device create(String _name, String _brand) {
        return create(_name,_brand, State.AVAILABLE);
    }
    public static Device create(String _name, String _brand, State _state) {
        return Device.builder()
                .id(UUID.randomUUID())
                .name(_name)
                .brand(_brand)
                .state(_state)
                .createdAt(OffsetDateTime.now())
                .build();
    }

}

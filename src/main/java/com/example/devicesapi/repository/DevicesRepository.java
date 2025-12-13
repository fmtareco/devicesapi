package com.example.devicesapi.repository;


import com.example.devicesapi.entities.Device;
import com.example.devicesapi.entities.Device.State;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;

/**
 * Repository design pattern :
 * - provides a centralized way to manage all data operations
 * - hides data storage particularities, streamlining changes of the data provider
 *  */
public interface DevicesRepository extends
        JpaRepository<Device, UUID>, JpaSpecificationExecutor<Device> {
    interface Specs {
        static Specification<Device> byBrand(String brand) {
            return (root, query, builder) ->
                    builder.like(root.get("brand"), brand);
        }

        static Specification<Device> byState(State state) {
            return (root, query, builder) ->
                    builder.equal(root.get("state"), state);
        }

        static Specification<Device> byBrandAndState(String brand, State state) {
            return (root, query, builder) ->
                    builder.and(
                            builder.like(root.get("brand"), brand),
                            builder.equal(root.get("state"), state));
        }
    }
}
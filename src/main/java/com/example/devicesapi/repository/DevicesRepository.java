package com.example.devicesapi.repository;


import com.example.devicesapi.entities.Device;
import com.example.devicesapi.entities.Device.State;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository design pattern :
 * - provides a centralized way to manage all data operations
 * - hides data storage particularities, streamlining changes of the data provider
 *  */
public interface DevicesRepository extends
        JpaRepository<Device, UUID>, JpaSpecificationExecutor<Device> {

    List<Device> findDeviceByNameAndBrand(String name, String brand);

    interface Specs {
        static Specification<Device> byBrand(String brand) {
            return (root, query, builder) ->
                    builder.like(root.get("brand"), "%"+brand+"%");
        }

        static Specification<Device> byState(State state) {
            return (root, query, builder) ->
                    builder.equal(root.get("state"), state);
        }

        static Specification<Device> byBrandAndState(String brand, State state) {
            return (root, query, builder) ->
                    builder.and(
                            builder.like(root.get("brand"), "%"+brand+"%"),
                            builder.equal(root.get("state"), state));
        }
        static Specification<Device> byFlexibleSearch(String name, String brand, State state) {
            return (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if  (name != null) {
                    predicates.add(builder.like(root.get("name"), "%"+name+"%"));
                }
                if (brand != null) {
                    predicates.add(builder.like(root.get("brand"), "%"+brand+"%"));
                }
                if (state != null) {
                    predicates.add(builder.equal(root.get("state"), state));
                }
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            };
        }
    }
}
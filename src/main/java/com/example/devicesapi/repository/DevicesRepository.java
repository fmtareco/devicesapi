package com.example.devicesapi.repository;


import com.example.devicesapi.entities.Device;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository design pattern :
 * - provides a centralized way to manage all data operations
 * - hides data storage particularities, streamlining changes of the data provider
 *  */
public interface DevicesRepository extends
        JpaRepository<Device, UUID>, JpaSpecificationExecutor<Device> {

    List<Device> findDeviceByNameAndBrand(String name, String brand);

    static Specification<Device> byFilters(Optional<String> name,
                                           Optional<String> brand,
                                           Optional<String> state) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            name.filter(n -> !n.isBlank())
                    .ifPresent(n ->
                            predicates.add(builder.like(builder.lower(root.get("name")), "%"+n.toLowerCase()+"%")));
            brand.filter(b -> !b.isBlank())
                    .ifPresent(b ->
                            predicates.add(builder.like(builder.lower(root.get("brand")), "%"+b.toLowerCase()+"%")));
            state.filter(s -> !s.isBlank())
                    .ifPresent(s ->
                            predicates.add(builder.equal(root.get("state"), Device.State.from(s))));
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
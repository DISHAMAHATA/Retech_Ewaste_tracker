package com.retech.repository;

import com.retech.entity.RecyclingFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecyclingFacilityRepository extends JpaRepository<RecyclingFacility, Long> {
    List<RecyclingFacility> findByActiveTrue();
    List<RecyclingFacility> findByCityIgnoreCaseAndActiveTrue(String city);
}

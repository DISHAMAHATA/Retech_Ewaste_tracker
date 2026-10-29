package com.retech.repository;

import com.retech.entity.CollectionCentre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionCentreRepository extends JpaRepository<CollectionCentre, Long> {
    List<CollectionCentre> findByActiveTrue();
    List<CollectionCentre> findByCityIgnoreCaseAndActiveTrue(String city);

    @Query("SELECT c FROM CollectionCentre c WHERE c.active = true " +
           "AND (:city IS NULL OR LOWER(c.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:category IS NULL OR LOWER(c.supportedCategories) LIKE LOWER(CONCAT('%', :category, '%')))")
    List<CollectionCentre> searchCentres(@Param("city") String city, @Param("category") String category);

    @Query("SELECT DISTINCT c.city FROM CollectionCentre c WHERE c.active = true ORDER BY c.city")
    List<String> findDistinctCities();
}

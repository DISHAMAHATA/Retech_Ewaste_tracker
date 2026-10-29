package com.retech.repository;

import com.retech.entity.ProcessingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProcessingRecordRepository extends JpaRepository<ProcessingRecord, Long> {
    List<ProcessingRecord> findByCollectionRequestIdOrderByRecordedAtDesc(Long requestId);
    List<ProcessingRecord> findByFacilityIdOrderByRecordedAtDesc(Long facilityId);
    List<ProcessingRecord> findByOperatorIdOrderByRecordedAtDesc(Long operatorId);

    @Query("SELECT COALESCE(SUM(p.weightReceivedKg), 0) FROM ProcessingRecord p WHERE p.facility.id = :facilityId")
    BigDecimal sumWeightReceivedByFacility(@Param("facilityId") Long facilityId);

    @Query("SELECT COALESCE(SUM(p.weightRecycledKg), 0) FROM ProcessingRecord p WHERE p.facility.id = :facilityId")
    BigDecimal sumWeightRecycledByFacility(@Param("facilityId") Long facilityId);

    @Query("SELECT COALESCE(SUM(p.weightReusedKg), 0) FROM ProcessingRecord p WHERE p.facility.id = :facilityId")
    BigDecimal sumWeightReusedByFacility(@Param("facilityId") Long facilityId);

    @Query("SELECT COALESCE(SUM(p.weightRecycledKg), 0) FROM ProcessingRecord p")
    BigDecimal sumTotalWeightRecycled();

    @Query("SELECT COALESCE(SUM(p.weightReusedKg), 0) FROM ProcessingRecord p")
    BigDecimal sumTotalWeightReused();

    @Query("SELECT COALESCE(SUM(p.weightDisposedKg), 0) FROM ProcessingRecord p")
    BigDecimal sumTotalWeightDisposed();
}

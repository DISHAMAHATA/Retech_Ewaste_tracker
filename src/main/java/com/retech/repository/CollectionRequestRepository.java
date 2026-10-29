package com.retech.repository;

import com.retech.entity.CollectionRequest;
import com.retech.entity.enums.EwasteCategory;
import com.retech.entity.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRequestRepository extends JpaRepository<CollectionRequest, Long> {

    Optional<CollectionRequest> findByTrackingCode(String trackingCode);

    boolean existsByTrackingCode(String trackingCode);

    List<CollectionRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Page<CollectionRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    List<CollectionRequest> findByAssignedAgentIdOrderByCreatedAtDesc(Long agentId);

    List<CollectionRequest> findByAssignedFacilityIdOrderByCreatedAtDesc(Long facilityId);

    List<CollectionRequest> findByStatus(RequestStatus status);

    @Query("SELECT r FROM CollectionRequest r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:category IS NULL OR r.category = :category) AND " +
           "(:search IS NULL OR LOWER(r.trackingCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(r.itemName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(r.customer.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(r.city) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY r.createdAt DESC")
    Page<CollectionRequest> searchRequests(@Param("status") RequestStatus status,
                                           @Param("category") EwasteCategory category,
                                           @Param("search") String search,
                                           Pageable pageable);

    // Customer Impact Stats
    @Query("SELECT COUNT(r) FROM CollectionRequest r WHERE r.customer.id = :customerId")
    long countByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT COUNT(r) FROM CollectionRequest r WHERE r.customer.id = :customerId AND r.status IN :statuses")
    long countByCustomerIdAndStatusIn(@Param("customerId") Long customerId, @Param("statuses") List<RequestStatus> statuses);

    @Query("SELECT COALESCE(SUM(COALESCE(r.actualWeightKg, r.estimatedWeightKg)), 0) FROM CollectionRequest r " +
           "WHERE r.customer.id = :customerId AND r.status IN :statuses")
    BigDecimal sumWeightByCustomerIdAndStatusIn(@Param("customerId") Long customerId, @Param("statuses") List<RequestStatus> statuses);

    // System-Wide Stats
    @Query("SELECT COUNT(r) FROM CollectionRequest r WHERE r.status IN :statuses")
    long countByStatusIn(@Param("statuses") List<RequestStatus> statuses);

    @Query("SELECT COALESCE(SUM(COALESCE(r.actualWeightKg, r.estimatedWeightKg)), 0) FROM CollectionRequest r " +
           "WHERE r.status IN :statuses")
    BigDecimal sumWeightByStatusIn(@Param("statuses") List<RequestStatus> statuses);

    @Query("SELECT r.category, COUNT(r), COALESCE(SUM(COALESCE(r.actualWeightKg, r.estimatedWeightKg)), 0) " +
           "FROM CollectionRequest r GROUP BY r.category")
    List<Object[]> getCategoryBreakdown();

    @Query("SELECT r.status, COUNT(r) FROM CollectionRequest r GROUP BY r.status")
    List<Object[]> getStatusCounts();
}

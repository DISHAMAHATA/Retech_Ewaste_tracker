package com.retech.repository;

import com.retech.entity.RequestStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestStatusHistoryRepository extends JpaRepository<RequestStatusHistory, Long> {
    List<RequestStatusHistory> findByCollectionRequestIdOrderByCreatedAtAsc(Long requestId);
    List<RequestStatusHistory> findByCollectionRequestTrackingCodeOrderByCreatedAtAsc(String trackingCode);
}

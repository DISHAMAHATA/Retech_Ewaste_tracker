package com.retech.dto;

import java.math.BigDecimal;
import java.util.Map;

public class SystemStatisticsDto {
    private long totalRequestsSubmitted;
    private long totalRequestsCollected;
    private long totalRequestsAtFacility;
    private long totalRequestsCompleted;
    private long totalRequestsPending;
    private long totalRequestsCancelledOrRejected;

    private BigDecimal totalWeightCollectedKg;
    private BigDecimal totalWeightRecycledKg;
    private BigDecimal totalWeightReusedKg;
    private BigDecimal totalWeightDisposedKg;

    private long totalRegisteredUsers;
    private long totalActiveCentres;
    private long totalActiveFacilities;

    private Map<String, Long> requestsByCategory;
    private Map<String, Long> requestsByStatus;

    public SystemStatisticsDto() {
    }

    // Getters and Setters

    public long getTotalRequestsSubmitted() {
        return totalRequestsSubmitted;
    }

    public void setTotalRequestsSubmitted(long totalRequestsSubmitted) {
        this.totalRequestsSubmitted = totalRequestsSubmitted;
    }

    public long getTotalRequestsCollected() {
        return totalRequestsCollected;
    }

    public void setTotalRequestsCollected(long totalRequestsCollected) {
        this.totalRequestsCollected = totalRequestsCollected;
    }

    public long getTotalRequestsAtFacility() {
        return totalRequestsAtFacility;
    }

    public void setTotalRequestsAtFacility(long totalRequestsAtFacility) {
        this.totalRequestsAtFacility = totalRequestsAtFacility;
    }

    public long getTotalRequestsCompleted() {
        return totalRequestsCompleted;
    }

    public void setTotalRequestsCompleted(long totalRequestsCompleted) {
        this.totalRequestsCompleted = totalRequestsCompleted;
    }

    public long getTotalRequestsPending() {
        return totalRequestsPending;
    }

    public void setTotalRequestsPending(long totalRequestsPending) {
        this.totalRequestsPending = totalRequestsPending;
    }

    public long getTotalRequestsCancelledOrRejected() {
        return totalRequestsCancelledOrRejected;
    }

    public void setTotalRequestsCancelledOrRejected(long totalRequestsCancelledOrRejected) {
        this.totalRequestsCancelledOrRejected = totalRequestsCancelledOrRejected;
    }

    public BigDecimal getTotalWeightCollectedKg() {
        return totalWeightCollectedKg;
    }

    public void setTotalWeightCollectedKg(BigDecimal totalWeightCollectedKg) {
        this.totalWeightCollectedKg = totalWeightCollectedKg;
    }

    public BigDecimal getTotalWeightRecycledKg() {
        return totalWeightRecycledKg;
    }

    public void setTotalWeightRecycledKg(BigDecimal totalWeightRecycledKg) {
        this.totalWeightRecycledKg = totalWeightRecycledKg;
    }

    public BigDecimal getTotalWeightReusedKg() {
        return totalWeightReusedKg;
    }

    public void setTotalWeightReusedKg(BigDecimal totalWeightReusedKg) {
        this.totalWeightReusedKg = totalWeightReusedKg;
    }

    public BigDecimal getTotalWeightDisposedKg() {
        return totalWeightDisposedKg;
    }

    public void setTotalWeightDisposedKg(BigDecimal totalWeightDisposedKg) {
        this.totalWeightDisposedKg = totalWeightDisposedKg;
    }

    public long getTotalRegisteredUsers() {
        return totalRegisteredUsers;
    }

    public void setTotalRegisteredUsers(long totalRegisteredUsers) {
        this.totalRegisteredUsers = totalRegisteredUsers;
    }

    public long getTotalActiveCentres() {
        return totalActiveCentres;
    }

    public void setTotalActiveCentres(long totalActiveCentres) {
        this.totalActiveCentres = totalActiveCentres;
    }

    public long getTotalActiveFacilities() {
        return totalActiveFacilities;
    }

    public void setTotalActiveFacilities(long totalActiveFacilities) {
        this.totalActiveFacilities = totalActiveFacilities;
    }

    public Map<String, Long> getRequestsByCategory() {
        return requestsByCategory;
    }

    public void setRequestsByCategory(Map<String, Long> requestsByCategory) {
        this.requestsByCategory = requestsByCategory;
    }

    public Map<String, Long> getRequestsByStatus() {
        return requestsByStatus;
    }

    public void setRequestsByStatus(Map<String, Long> requestsByStatus) {
        this.requestsByStatus = requestsByStatus;
    }
}

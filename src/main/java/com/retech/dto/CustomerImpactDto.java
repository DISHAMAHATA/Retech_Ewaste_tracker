package com.retech.dto;

import java.math.BigDecimal;

public class CustomerImpactDto {
    private long totalRequestsSubmitted;
    private long totalRequestsCollected;
    private long totalRequestsRecycled;
    private long totalRequestsPending;
    private BigDecimal totalWeightCollectedKg;
    private BigDecimal totalWeightRecycledKg;
    private BigDecimal estimatedCo2AvoidedKg; // Transparently documented calculation: weight recycled * 1.44 kg CO2e/kg e-waste factor

    public CustomerImpactDto() {
    }

    public CustomerImpactDto(long totalRequestsSubmitted, long totalRequestsCollected, long totalRequestsRecycled,
                             long totalRequestsPending, BigDecimal totalWeightCollectedKg,
                             BigDecimal totalWeightRecycledKg, BigDecimal estimatedCo2AvoidedKg) {
        this.totalRequestsSubmitted = totalRequestsSubmitted;
        this.totalRequestsCollected = totalRequestsCollected;
        this.totalRequestsRecycled = totalRequestsRecycled;
        this.totalRequestsPending = totalRequestsPending;
        this.totalWeightCollectedKg = totalWeightCollectedKg;
        this.totalWeightRecycledKg = totalWeightRecycledKg;
        this.estimatedCo2AvoidedKg = estimatedCo2AvoidedKg;
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

    public long getTotalRequestsRecycled() {
        return totalRequestsRecycled;
    }

    public void setTotalRequestsRecycled(long totalRequestsRecycled) {
        this.totalRequestsRecycled = totalRequestsRecycled;
    }

    public long getTotalRequestsPending() {
        return totalRequestsPending;
    }

    public void setTotalRequestsPending(long totalRequestsPending) {
        this.totalRequestsPending = totalRequestsPending;
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

    public BigDecimal getEstimatedCo2AvoidedKg() {
        return estimatedCo2AvoidedKg;
    }

    public void setEstimatedCo2AvoidedKg(BigDecimal estimatedCo2AvoidedKg) {
        this.estimatedCo2AvoidedKg = estimatedCo2AvoidedKg;
    }
}

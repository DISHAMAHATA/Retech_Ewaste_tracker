package com.retech.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.retech.entity.enums.ProcessingOutcome;
import com.retech.entity.enums.ProcessingStage;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "processing_records")
public class ProcessingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private CollectionRequest collectionRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private RecyclingFacility facility;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operator_id", nullable = false)
    private User operator;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false, length = 30)
    private ProcessingStage stage;

    @Column(name = "quantity_received", nullable = false)
    private Integer quantityReceived;

    @Column(name = "weight_received_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal weightReceivedKg;

    @Column(name = "weight_reused_kg", precision = 8, scale = 2)
    private BigDecimal weightReusedKg = BigDecimal.ZERO;

    @Column(name = "weight_recycled_kg", precision = 8, scale = 2)
    private BigDecimal weightRecycledKg = BigDecimal.ZERO;

    @Column(name = "weight_disposed_kg", precision = 8, scale = 2)
    private BigDecimal weightDisposedKg = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", nullable = false, length = 50)
    private ProcessingOutcome outcome;

    @Column(name = "verification_notes", length = 500)
    private String verificationNotes;

    @Column(name = "recorded_at", updatable = false)
    private LocalDateTime recordedAt;

    public ProcessingRecord() {
    }

    public ProcessingRecord(CollectionRequest collectionRequest, RecyclingFacility facility, User operator,
                            ProcessingStage stage, Integer quantityReceived, BigDecimal weightReceivedKg,
                            BigDecimal weightReusedKg, BigDecimal weightRecycledKg, BigDecimal weightDisposedKg,
                            ProcessingOutcome outcome, String verificationNotes) {
        this.collectionRequest = collectionRequest;
        this.facility = facility;
        this.operator = operator;
        this.stage = stage;
        this.quantityReceived = quantityReceived;
        this.weightReceivedKg = weightReceivedKg;
        this.weightReusedKg = weightReusedKg != null ? weightReusedKg : BigDecimal.ZERO;
        this.weightRecycledKg = weightRecycledKg != null ? weightRecycledKg : BigDecimal.ZERO;
        this.weightDisposedKg = weightDisposedKg != null ? weightDisposedKg : BigDecimal.ZERO;
        this.outcome = outcome;
        this.verificationNotes = verificationNotes;
    }

    @PrePersist
    protected void onCreate() {
        this.recordedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CollectionRequest getCollectionRequest() {
        return collectionRequest;
    }

    public void setCollectionRequest(CollectionRequest collectionRequest) {
        this.collectionRequest = collectionRequest;
    }

    public RecyclingFacility getFacility() {
        return facility;
    }

    public void setFacility(RecyclingFacility facility) {
        this.facility = facility;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public ProcessingStage getStage() {
        return stage;
    }

    public void setStage(ProcessingStage stage) {
        this.stage = stage;
    }

    public Integer getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Integer quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public BigDecimal getWeightReceivedKg() {
        return weightReceivedKg;
    }

    public void setWeightReceivedKg(BigDecimal weightReceivedKg) {
        this.weightReceivedKg = weightReceivedKg;
    }

    public BigDecimal getWeightReusedKg() {
        return weightReusedKg;
    }

    public void setWeightReusedKg(BigDecimal weightReusedKg) {
        this.weightReusedKg = weightReusedKg;
    }

    public BigDecimal getWeightRecycledKg() {
        return weightRecycledKg;
    }

    public void setWeightRecycledKg(BigDecimal weightRecycledKg) {
        this.weightRecycledKg = weightRecycledKg;
    }

    public BigDecimal getWeightDisposedKg() {
        return weightDisposedKg;
    }

    public void setWeightDisposedKg(BigDecimal weightDisposedKg) {
        this.weightDisposedKg = weightDisposedKg;
    }

    public ProcessingOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(ProcessingOutcome outcome) {
        this.outcome = outcome;
    }

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
}

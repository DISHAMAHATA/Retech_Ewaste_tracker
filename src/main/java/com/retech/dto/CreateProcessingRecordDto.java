package com.retech.dto;

import com.retech.entity.enums.ProcessingOutcome;
import com.retech.entity.enums.ProcessingStage;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CreateProcessingRecordDto {

    @NotNull(message = "Request ID is required")
    private Long requestId;

    @NotNull(message = "Facility ID is required")
    private Long facilityId;

    @NotNull(message = "Processing stage is required")
    private ProcessingStage stage;

    @NotNull(message = "Quantity received is required")
    @Min(value = 1, message = "Quantity received must be at least 1")
    private Integer quantityReceived;

    @NotNull(message = "Weight received is required")
    @DecimalMin(value = "0.01", message = "Weight received must be positive")
    private BigDecimal weightReceivedKg;

    @DecimalMin(value = "0.00", message = "Reused weight cannot be negative")
    private BigDecimal weightReusedKg;

    @DecimalMin(value = "0.00", message = "Recycled weight cannot be negative")
    private BigDecimal weightRecycledKg;

    @DecimalMin(value = "0.00", message = "Disposed weight cannot be negative")
    private BigDecimal weightDisposedKg;

    @NotNull(message = "Processing outcome is required")
    private ProcessingOutcome outcome;

    @Size(max = 500, message = "Verification notes cannot exceed 500 characters")
    private String verificationNotes;

    public CreateProcessingRecordDto() {
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
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
}

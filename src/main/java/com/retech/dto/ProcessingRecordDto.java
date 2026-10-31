package com.retech.dto;

import com.retech.entity.ProcessingRecord;
import com.retech.entity.enums.ProcessingOutcome;
import com.retech.entity.enums.ProcessingStage;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProcessingRecordDto {
    private Long id;
    private Long requestId;
    private String trackingCode;
    private String itemName;
    private Long facilityId;
    private String facilityName;
    private Long operatorId;
    private String operatorName;
    private ProcessingStage stage;
    private String stageDescription;
    private Integer quantityReceived;
    private BigDecimal weightReceivedKg;
    private BigDecimal weightReusedKg;
    private BigDecimal weightRecycledKg;
    private BigDecimal weightDisposedKg;
    private ProcessingOutcome outcome;
    private String outcomeDescription;
    private String verificationNotes;
    private LocalDateTime recordedAt;

    public ProcessingRecordDto() {
    }

    public static ProcessingRecordDto fromEntity(ProcessingRecord record) {
        ProcessingRecordDto dto = new ProcessingRecordDto();
        dto.setId(record.getId());
        try {
            if (record.getCollectionRequest() != null) {
                dto.setRequestId(record.getCollectionRequest().getId());
                dto.setTrackingCode(record.getCollectionRequest().getTrackingCode());
                dto.setItemName(record.getCollectionRequest().getItemName());
            }
        } catch (Exception ignored) {}

        try {
            if (record.getFacility() != null) {
                dto.setFacilityId(record.getFacility().getId());
                dto.setFacilityName(record.getFacility().getName());
            }
        } catch (Exception ignored) {}

        try {
            if (record.getOperator() != null) {
                dto.setOperatorId(record.getOperator().getId());
                dto.setOperatorName(record.getOperator().getFullName());
            }
        } catch (Exception ignored) {}
        dto.setStage(record.getStage());
        dto.setStageDescription(record.getStage().getDescription());
        dto.setQuantityReceived(record.getQuantityReceived());
        dto.setWeightReceivedKg(record.getWeightReceivedKg());
        dto.setWeightReusedKg(record.getWeightReusedKg());
        dto.setWeightRecycledKg(record.getWeightRecycledKg());
        dto.setWeightDisposedKg(record.getWeightDisposedKg());
        dto.setOutcome(record.getOutcome());
        dto.setOutcomeDescription(record.getOutcome().getDescription());
        dto.setVerificationNotes(record.getVerificationNotes());
        dto.setRecordedAt(record.getRecordedAt());
        return dto;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public ProcessingStage getStage() {
        return stage;
    }

    public void setStage(ProcessingStage stage) {
        this.stage = stage;
    }

    public String getStageDescription() {
        return stageDescription;
    }

    public void setStageDescription(String stageDescription) {
        this.stageDescription = stageDescription;
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

    public String getOutcomeDescription() {
        return outcomeDescription;
    }

    public void setOutcomeDescription(String outcomeDescription) {
        this.outcomeDescription = outcomeDescription;
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

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}

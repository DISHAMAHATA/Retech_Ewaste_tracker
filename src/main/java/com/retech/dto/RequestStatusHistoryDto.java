package com.retech.dto;

import com.retech.entity.RequestStatusHistory;
import com.retech.entity.enums.RequestStatus;
import java.time.LocalDateTime;

public class RequestStatusHistoryDto {
    private Long id;
    private RequestStatus previousStatus;
    private String previousStatusDisplay;
    private RequestStatus newStatus;
    private String newStatusDisplay;
    private Long changedByUserId;
    private String changedByName;
    private String remarks;
    private LocalDateTime createdAt;

    public RequestStatusHistoryDto() {
    }

    public static RequestStatusHistoryDto fromEntity(RequestStatusHistory entity) {
        RequestStatusHistoryDto dto = new RequestStatusHistoryDto();
        dto.setId(entity.getId());
        dto.setPreviousStatus(entity.getPreviousStatus());
        dto.setPreviousStatusDisplay(entity.getPreviousStatus() != null ? entity.getPreviousStatus().getDisplayName() : null);
        dto.setNewStatus(entity.getNewStatus());
        dto.setNewStatusDisplay(entity.getNewStatus().getDisplayName());
        dto.setChangedByUserId(entity.getChangedByUser() != null ? entity.getChangedByUser().getId() : null);
        dto.setChangedByName(entity.getChangedByName());
        dto.setRemarks(entity.getRemarks());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(RequestStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getPreviousStatusDisplay() {
        return previousStatusDisplay;
    }

    public void setPreviousStatusDisplay(String previousStatusDisplay) {
        this.previousStatusDisplay = previousStatusDisplay;
    }

    public RequestStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(RequestStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getNewStatusDisplay() {
        return newStatusDisplay;
    }

    public void setNewStatusDisplay(String newStatusDisplay) {
        this.newStatusDisplay = newStatusDisplay;
    }

    public Long getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(Long changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public void setChangedByName(String changedByName) {
        this.changedByName = changedByName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

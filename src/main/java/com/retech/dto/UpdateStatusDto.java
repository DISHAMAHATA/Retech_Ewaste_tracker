package com.retech.dto;

import com.retech.entity.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateStatusDto {

    @NotNull(message = "New status is required")
    private RequestStatus newStatus;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    private BigDecimal actualWeightKg;
    private LocalDate actualPickupDate;

    public UpdateStatusDto() {
    }

    public UpdateStatusDto(RequestStatus newStatus, String remarks) {
        this.newStatus = newStatus;
        this.remarks = remarks;
    }

    public RequestStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(RequestStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigDecimal getActualWeightKg() {
        return actualWeightKg;
    }

    public void setActualWeightKg(BigDecimal actualWeightKg) {
        this.actualWeightKg = actualWeightKg;
    }

    public LocalDate getActualPickupDate() {
        return actualPickupDate;
    }

    public void setActualPickupDate(LocalDate actualPickupDate) {
        this.actualPickupDate = actualPickupDate;
    }
}

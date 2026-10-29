package com.retech.dto;

import jakarta.validation.constraints.NotNull;

public class AssignFacilityDto {

    @NotNull(message = "Facility ID is required")
    private Long facilityId;

    public AssignFacilityDto() {
    }

    public AssignFacilityDto(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }
}

package com.retech.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AssignAgentDto {

    @NotNull(message = "Agent ID is required")
    private Long agentId;

    private LocalDate scheduledPickupDate;

    public AssignAgentDto() {
    }

    public AssignAgentDto(Long agentId, LocalDate scheduledPickupDate) {
        this.agentId = agentId;
        this.scheduledPickupDate = scheduledPickupDate;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public LocalDate getScheduledPickupDate() {
        return scheduledPickupDate;
    }

    public void setScheduledPickupDate(LocalDate scheduledPickupDate) {
        this.scheduledPickupDate = scheduledPickupDate;
    }
}

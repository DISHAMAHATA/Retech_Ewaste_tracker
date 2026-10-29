package com.retech.entity.enums;

import java.util.Set;

public enum RequestStatus {
    REQUEST_SUBMITTED("Request Submitted", 1),
    REQUEST_APPROVED("Request Approved", 2),
    REQUEST_REJECTED("Request Rejected", -1),
    PICKUP_SCHEDULED("Pickup Scheduled", 3),
    COLLECTED("Collected by Agent", 4),
    RECEIVED_AT_FACILITY("Received at Facility", 5),
    UNDER_PROCESSING("Under Processing", 6),
    RECYCLING_COMPLETED("Recycling Completed", 7),
    CANCELLED("Cancelled", 0);

    private final String displayName;
    private final int stageOrder;

    RequestStatus(String displayName, int stageOrder) {
        this.displayName = displayName;
        this.stageOrder = stageOrder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getStageOrder() {
        return stageOrder;
    }

    public boolean isTerminal() {
        return this == RECYCLING_COMPLETED || this == REQUEST_REJECTED || this == CANCELLED;
    }

    public boolean isCancellableByCustomer() {
        return this == REQUEST_SUBMITTED || this == REQUEST_APPROVED || this == PICKUP_SCHEDULED;
    }

    /**
     * Defines strict permitted next transitions in the workflow.
     */
    public boolean canTransitionTo(RequestStatus target) {
        if (this == target) {
            // Re-scheduling pickup to another date/agent is permitted
            return this == PICKUP_SCHEDULED;
        }

        return switch (this) {
            case REQUEST_SUBMITTED -> target == REQUEST_APPROVED || target == REQUEST_REJECTED || target == CANCELLED;
            case REQUEST_APPROVED -> target == PICKUP_SCHEDULED || target == CANCELLED;
            case PICKUP_SCHEDULED -> target == COLLECTED || target == CANCELLED;
            case COLLECTED -> target == RECEIVED_AT_FACILITY;
            case RECEIVED_AT_FACILITY -> target == UNDER_PROCESSING;
            case UNDER_PROCESSING -> target == RECYCLING_COMPLETED;
            case REQUEST_REJECTED, RECYCLING_COMPLETED, CANCELLED -> false;
        };
    }
}

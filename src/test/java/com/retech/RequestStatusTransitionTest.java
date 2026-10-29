package com.retech;

import com.retech.entity.enums.RequestStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestStatusTransitionTest {

    @Test
    @DisplayName("Verify strict permitted status transition sequences")
    void testPermittedTransitions() {
        // Step 1: SUBMITTED -> APPROVED, REJECTED, CANCELLED
        assertTrue(RequestStatus.REQUEST_SUBMITTED.canTransitionTo(RequestStatus.REQUEST_APPROVED));
        assertTrue(RequestStatus.REQUEST_SUBMITTED.canTransitionTo(RequestStatus.REQUEST_REJECTED));
        assertTrue(RequestStatus.REQUEST_SUBMITTED.canTransitionTo(RequestStatus.CANCELLED));
        assertFalse(RequestStatus.REQUEST_SUBMITTED.canTransitionTo(RequestStatus.COLLECTED));
        assertFalse(RequestStatus.REQUEST_SUBMITTED.canTransitionTo(RequestStatus.RECYCLING_COMPLETED));

        // Step 2: APPROVED -> SCHEDULED, CANCELLED
        assertTrue(RequestStatus.REQUEST_APPROVED.canTransitionTo(RequestStatus.PICKUP_SCHEDULED));
        assertTrue(RequestStatus.REQUEST_APPROVED.canTransitionTo(RequestStatus.CANCELLED));
        assertFalse(RequestStatus.REQUEST_APPROVED.canTransitionTo(RequestStatus.COLLECTED));

        // Step 3: SCHEDULED -> COLLECTED, CANCELLED, RESCHEDULED
        assertTrue(RequestStatus.PICKUP_SCHEDULED.canTransitionTo(RequestStatus.COLLECTED));
        assertTrue(RequestStatus.PICKUP_SCHEDULED.canTransitionTo(RequestStatus.CANCELLED));
        assertTrue(RequestStatus.PICKUP_SCHEDULED.canTransitionTo(RequestStatus.PICKUP_SCHEDULED)); // Reschedule
        assertFalse(RequestStatus.PICKUP_SCHEDULED.canTransitionTo(RequestStatus.RECYCLING_COMPLETED));

        // Step 4: COLLECTED -> RECEIVED_AT_FACILITY
        assertTrue(RequestStatus.COLLECTED.canTransitionTo(RequestStatus.RECEIVED_AT_FACILITY));
        assertFalse(RequestStatus.COLLECTED.canTransitionTo(RequestStatus.CANCELLED)); // Cannot cancel once collected

        // Step 5: RECEIVED_AT_FACILITY -> UNDER_PROCESSING
        assertTrue(RequestStatus.RECEIVED_AT_FACILITY.canTransitionTo(RequestStatus.UNDER_PROCESSING));
        assertFalse(RequestStatus.RECEIVED_AT_FACILITY.canTransitionTo(RequestStatus.COLLECTED));

        // Step 6: UNDER_PROCESSING -> RECYCLING_COMPLETED
        assertTrue(RequestStatus.UNDER_PROCESSING.canTransitionTo(RequestStatus.RECYCLING_COMPLETED));
        assertFalse(RequestStatus.UNDER_PROCESSING.canTransitionTo(RequestStatus.REQUEST_SUBMITTED));

        // Terminal States cannot transition to anything
        assertFalse(RequestStatus.RECYCLING_COMPLETED.canTransitionTo(RequestStatus.UNDER_PROCESSING));
        assertFalse(RequestStatus.RECYCLING_COMPLETED.canTransitionTo(RequestStatus.CANCELLED));
        assertFalse(RequestStatus.CANCELLED.canTransitionTo(RequestStatus.REQUEST_SUBMITTED));
        assertFalse(RequestStatus.REQUEST_REJECTED.canTransitionTo(RequestStatus.REQUEST_APPROVED));
    }

    @Test
    @DisplayName("Verify cancellation eligibility helper")
    void testCancellableByCustomer() {
        assertTrue(RequestStatus.REQUEST_SUBMITTED.isCancellableByCustomer());
        assertTrue(RequestStatus.REQUEST_APPROVED.isCancellableByCustomer());
        assertTrue(RequestStatus.PICKUP_SCHEDULED.isCancellableByCustomer());

        assertFalse(RequestStatus.COLLECTED.isCancellableByCustomer());
        assertFalse(RequestStatus.RECEIVED_AT_FACILITY.isCancellableByCustomer());
        assertFalse(RequestStatus.UNDER_PROCESSING.isCancellableByCustomer());
        assertFalse(RequestStatus.RECYCLING_COMPLETED.isCancellableByCustomer());
        assertFalse(RequestStatus.CANCELLED.isCancellableByCustomer());
        assertFalse(RequestStatus.REQUEST_REJECTED.isCancellableByCustomer());
    }
}

package com.ridelink.ridemanagementservice.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Ride lifecycle used across RideLink.
 * Valid transitions are enforced centrally here so the rule lives in one place.
 */
public enum RideStatus {
    REQUESTED,
    ASSIGNED,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    private static final Map<RideStatus, Set<RideStatus>> ALLOWED_TRANSITIONS = Map.of(
            REQUESTED,   EnumSet.of(ASSIGNED, CANCELLED),
            ASSIGNED,    EnumSet.of(ACCEPTED, CANCELLED),
            ACCEPTED,    EnumSet.of(IN_PROGRESS, CANCELLED),
            IN_PROGRESS, EnumSet.of(COMPLETED, CANCELLED),
            COMPLETED,   EnumSet.noneOf(RideStatus.class),
            CANCELLED,   EnumSet.noneOf(RideStatus.class)
    );

    public boolean canTransitionTo(RideStatus next) {
        return ALLOWED_TRANSITIONS.getOrDefault(this, Set.of()).contains(next);
    }
}

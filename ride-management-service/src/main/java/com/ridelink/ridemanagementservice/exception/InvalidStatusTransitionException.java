package com.ridelink.ridemanagementservice.exception;

import com.ridelink.ridemanagementservice.model.RideStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(RideStatus from, RideStatus to) {
        super("Cannot transition ride from " + from + " to " + to);
    }
}

package com.ridelink.ridemanagementservice.dto;

import com.ridelink.ridemanagementservice.model.Ride;
import com.ridelink.ridemanagementservice.model.RideStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RideResponse {
    private String rideId;
    private String customerId;
    private String driverId;
    private String pickupLocation;
    private String destinationLocation;
    private RideStatus status;
    private Double estimatedFare;
    private Double finalFare;
    private Instant requestedAt;
    private Instant updatedAt;

    public static RideResponse from(Ride ride) {
        return RideResponse.builder()
                .rideId(ride.getId())
                .customerId(ride.getCustomerId())
                .driverId(ride.getDriverId())
                .pickupLocation(ride.getPickupLocation())
                .destinationLocation(ride.getDestinationLocation())
                .status(ride.getStatus())
                .estimatedFare(ride.getEstimatedFare())
                .finalFare(ride.getFinalFare())
                .requestedAt(ride.getRequestedAt())
                .updatedAt(ride.getUpdatedAt())
                .build();
    }
}

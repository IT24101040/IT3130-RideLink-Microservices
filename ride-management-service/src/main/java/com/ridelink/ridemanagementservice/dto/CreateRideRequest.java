package com.ridelink.ridemanagementservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRideRequest {

    @NotBlank(message = "customerId is required")
    private String customerId;

    @NotBlank(message = "pickupLocation is required")
    private String pickupLocation;

    @NotBlank(message = "destinationLocation is required")
    private String destinationLocation;

    // Optional: pass through an estimate obtained earlier from Fare & Payment Service
    private Double estimatedFare;
}

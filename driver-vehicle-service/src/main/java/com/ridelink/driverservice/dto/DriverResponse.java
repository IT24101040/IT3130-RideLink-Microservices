package com.ridelink.driverservice.dto;

import com.ridelink.driverservice.model.AvailabilityStatus;
import java.time.Instant;

// Full view: only for the driver themself and admins
public record DriverResponse(String driverId, String userId, String fullName, String phoneNumber,
                             String licenseNumber, String city, VehicleResponse vehicle,
                             AvailabilityStatus availabilityStatus, LocationResponse currentLocation,
                             Instant createdAt, Instant updatedAt) {}
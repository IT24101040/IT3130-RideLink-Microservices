package com.ridelink.driverservice.dto;

import com.ridelink.driverservice.model.AvailabilityStatus;

// Safe view for other services and passengers: no phone, licence or userId
public record DriverPublicResponse(String driverId, String fullName, String city, VehicleResponse vehicle,
                                   AvailabilityStatus availabilityStatus, LocationResponse currentLocation,
                                   Double distanceKm) {}
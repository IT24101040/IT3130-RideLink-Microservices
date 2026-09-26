package com.ridelink.driverservice.dto;

import java.time.Instant;

public record LocationResponse(double latitude, double longitude, Instant updatedAt) {}
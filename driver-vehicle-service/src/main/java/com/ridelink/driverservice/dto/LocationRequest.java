package com.ridelink.driverservice.dto;

import jakarta.validation.constraints.*;

public record LocationRequest(
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        String city) {}   // optional: also updates the service area
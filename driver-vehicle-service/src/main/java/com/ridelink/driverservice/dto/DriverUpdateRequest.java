package com.ridelink.driverservice.dto;

import jakarta.validation.constraints.*;

public record DriverUpdateRequest(
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "must be 9-15 digits") String phoneNumber,
        @NotBlank String city) {}
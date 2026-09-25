package com.ridelink.driverservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record DriverRegistrationRequest(
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "must be 9-15 digits") String phoneNumber,
        @NotBlank String licenseNumber,
        @NotBlank String city,
        @NotNull @Valid VehicleRequest vehicle) {}
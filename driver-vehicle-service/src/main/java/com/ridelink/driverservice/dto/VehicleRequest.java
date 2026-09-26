package com.ridelink.driverservice.dto;

import com.ridelink.driverservice.model.VehicleType;
import jakarta.validation.constraints.*;

public record VehicleRequest(
        @NotBlank String plateNumber,
        @NotBlank String make,
        @NotBlank String model,
        String color,
        @NotNull @Min(1990) Integer year,
        @NotNull VehicleType type,
        @NotNull @Min(1) @Max(12) Integer seatCapacity) {}
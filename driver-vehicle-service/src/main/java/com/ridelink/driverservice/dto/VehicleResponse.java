package com.ridelink.driverservice.dto;

import com.ridelink.driverservice.model.VehicleType;

public record VehicleResponse(String plateNumber, String make, String model, String color,
                              Integer year, VehicleType type, Integer seatCapacity) {}
package com.ridelink.driverservice.model;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Vehicle {
    private String plateNumber;
    private String make;
    private String model;
    private String color;
    private Integer year;
    private VehicleType type;
    private Integer seatCapacity;
}
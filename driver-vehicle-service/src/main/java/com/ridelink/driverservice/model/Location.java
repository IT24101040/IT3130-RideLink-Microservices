package com.ridelink.driverservice.model;

import lombok.*;
import java.time.Instant;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Location {
    private double latitude;
    private double longitude;
    private Instant updatedAt;
}
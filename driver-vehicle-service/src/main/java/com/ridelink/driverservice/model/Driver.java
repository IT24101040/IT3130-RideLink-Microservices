package com.ridelink.driverservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "drivers")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Driver {

    @Id
    private String id;                       // Mongo internal id, never exposed

    @Indexed(unique = true)
    private String driverId;                 // UUID, shared with other services

    @Indexed(unique = true)
    private String userId;                   // "sub" claim from the Account Service JWT

    private String fullName;
    private String phoneNumber;

    @Indexed(unique = true)
    private String licenseNumber;

    private String city;                     // service area
    private Vehicle vehicle;
    private AvailabilityStatus availabilityStatus;
    private Location currentLocation;
    private Instant createdAt;
    private Instant updatedAt;
}
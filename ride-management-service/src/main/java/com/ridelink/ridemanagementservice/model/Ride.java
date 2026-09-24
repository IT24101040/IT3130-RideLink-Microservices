package com.ridelink.ridemanagementservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Owned exclusively by Ride Management Service.
 * No other service may read or write this collection directly (per architecture.md).
 * customerId / driverId are UUID strings that reference records in Account /
 * Driver & Vehicle Service — never a foreign-service database id.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rides")
public class Ride {

    @Id
    private String id; // UUID string, generated at creation time

    private String customerId;
    private String driverId; // null until assigned

    private String pickupLocation;
    private String destinationLocation;

    private RideStatus status;

    private Double estimatedFare; // copied from Fare Service estimate at creation, optional
    private Double finalFare;     // set once Fare Service calculates it on completion

    private Instant requestedAt;
    private Instant updatedAt;
}

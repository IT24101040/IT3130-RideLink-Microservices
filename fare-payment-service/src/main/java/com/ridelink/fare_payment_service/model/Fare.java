package com.ridelink.fare_payment_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fares")
public class Fare {

    @Id
    private String id;

    private String rideId;

    private double distanceInKm;

    private double estimatedFare;

    private double finalFare;

    private PaymentStatus paymentStatus;

    private PaymentMethod paymentMethod;

    private String receiptNumber;
}


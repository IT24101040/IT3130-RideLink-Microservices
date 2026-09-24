package com.ridelink.fare_payment_service.dto;

import com.ridelink.fare_payment_service.model.PaymentMethod;
import com.ridelink.fare_payment_service.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareResponse {

    private String id;
    private String rideId;
    private double distanceInKm;
    private double estimatedFare;
    private double finalFare;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String receiptNumber;
}

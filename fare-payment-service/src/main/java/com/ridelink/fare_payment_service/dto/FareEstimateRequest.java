package com.ridelink.fare_payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareEstimateRequest {

    @NotBlank(message = "Ride ID is mandatory")
    private String rideId;

    @NotNull(message = "Distance in kilometers is mandatory")
    @Positive(message = "Distance must be greater than zero")
    private Double distanceInKm;
}

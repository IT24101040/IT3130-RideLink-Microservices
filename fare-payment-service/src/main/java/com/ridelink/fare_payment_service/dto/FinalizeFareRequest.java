package com.ridelink.fare_payment_service.dto;

import com.ridelink.fare_payment_service.model.PaymentMethod;
import com.ridelink.fare_payment_service.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalizeFareRequest {

    @NotNull(message = "Final fare is mandatory")
    @PositiveOrZero(message = "Final fare cannot be negative")
    private Double finalFare;

    @NotNull(message = "Payment status is mandatory")
    private PaymentStatus paymentStatus;

    private PaymentMethod paymentMethod;
}

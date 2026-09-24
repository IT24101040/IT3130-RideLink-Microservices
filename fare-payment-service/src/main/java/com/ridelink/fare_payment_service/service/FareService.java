package com.ridelink.fare_payment_service.service;

import com.ridelink.fare_payment_service.dto.FareEstimateRequest;
import com.ridelink.fare_payment_service.dto.FareResponse;
import com.ridelink.fare_payment_service.dto.FinalizeFareRequest;

public interface FareService {

    /**
     * Calculates the estimated fare based on distance (Base 100 LKR + 50 LKR/km),
     * persists the fare record with PENDING status, and returns the fare details.
     *
     * @param request the fare estimation request containing rideId and distanceInKm
     * @return the saved FareResponse
     */
    FareResponse calculateAndSaveEstimate(FareEstimateRequest request);

    /**
     * Retrieves fare details by MongoDB identifier.
     *
     * @param id the MongoDB ID
     * @return the fare details
     */
    FareResponse getFareById(String id);

    /**
     * Retrieves fare details by ride identifier.
     *
     * @param rideId the ride UUID string
     * @return the fare details
     */
    FareResponse getFareByRideId(String rideId);

    /**
     * Finalizes the fare with final fare amount and updates payment status (PAID or FAILED).
     *
     * @param id the MongoDB ID
     * @param request the request containing final fare and payment status
     * @return the updated fare details
     */
    FareResponse finalizeFare(String id, FinalizeFareRequest request);
}

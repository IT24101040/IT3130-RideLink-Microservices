package com.ridelink.fare_payment_service.service;

import com.ridelink.fare_payment_service.dto.FareEstimateRequest;
import com.ridelink.fare_payment_service.dto.FareResponse;
import com.ridelink.fare_payment_service.dto.FinalizeFareRequest;
import com.ridelink.fare_payment_service.exception.ResourceNotFoundException;
import com.ridelink.fare_payment_service.model.Fare;
import com.ridelink.fare_payment_service.model.PaymentStatus;
import com.ridelink.fare_payment_service.repository.FareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FareServiceImpl implements FareService {

    private static final double BASE_FARE = 100.0;
    private static final double RATE_PER_KM = 50.0;

    private final FareRepository fareRepository;

    @Override
    public FareResponse calculateAndSaveEstimate(FareEstimateRequest request) {
        log.info("Calculating fare estimate for rideId: {} with distance: {} km", request.getRideId(), request.getDistanceInKm());

        double estimatedFare = calculateEstimate(request.getDistanceInKm());

        Fare fare = Fare.builder()
                .rideId(request.getRideId())
                .distanceInKm(request.getDistanceInKm())
                .estimatedFare(estimatedFare)
                .finalFare(0.0)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Fare savedFare = fareRepository.save(fare);
        log.info("Fare estimate successfully saved with id: {}", savedFare.getId());

        return mapToResponse(savedFare);
    }

    @Override
    public FareResponse getFareById(String id) {
        log.info("Fetching fare details by id: {}", id);
        Fare fare = fareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fare not found with ID: " + id));
        return mapToResponse(fare);
    }

    @Override
    public FareResponse getFareByRideId(String rideId) {
        log.info("Fetching fare details by rideId: {}", rideId);
        Fare fare = fareRepository.findByRideId(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Fare not found for ride ID: " + rideId));
        return mapToResponse(fare);
    }

    @Override
    public FareResponse finalizeFare(String id, FinalizeFareRequest request) {
        log.info("Finalizing fare for id: {} with finalFare: {}, status: {}, method: {}",
                id, request.getFinalFare(), request.getPaymentStatus(), request.getPaymentMethod());

        if (request.getPaymentStatus() == PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment status must be PAID or FAILED when finalizing fare.");
        }

        Fare fare = fareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fare not found with ID: " + id));

        fare.setFinalFare(request.getFinalFare());
        fare.setPaymentStatus(request.getPaymentStatus());
        fare.setPaymentMethod(request.getPaymentMethod());

        if (request.getPaymentStatus() == PaymentStatus.PAID) {
            String receipt = "RCPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            fare.setReceiptNumber(receipt);
            log.info("Generated receipt: {} for fare id: {}", receipt, fare.getId());
        } else {
            fare.setReceiptNumber(null);
        }

        Fare updatedFare = fareRepository.save(fare);
        log.info("Fare successfully finalized for id: {}", updatedFare.getId());

        return mapToResponse(updatedFare);
    }

    private double calculateEstimate(double distanceInKm) {
        return BASE_FARE + (distanceInKm * RATE_PER_KM);
    }

    private FareResponse mapToResponse(Fare fare) {
        return FareResponse.builder()
                .id(fare.getId())
                .rideId(fare.getRideId())
                .distanceInKm(fare.getDistanceInKm())
                .estimatedFare(fare.getEstimatedFare())
                .finalFare(fare.getFinalFare())
                .paymentStatus(fare.getPaymentStatus())
                .paymentMethod(fare.getPaymentMethod())
                .receiptNumber(fare.getReceiptNumber())
                .build();
    }
}


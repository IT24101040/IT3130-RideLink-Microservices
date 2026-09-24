package com.ridelink.fare_payment_service.controller;

import com.ridelink.fare_payment_service.dto.FareEstimateRequest;
import com.ridelink.fare_payment_service.dto.FareResponse;
import com.ridelink.fare_payment_service.dto.FinalizeFareRequest;
import com.ridelink.fare_payment_service.model.PaymentStatus;
import com.ridelink.fare_payment_service.service.FareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareControllerTest {

    @Mock
    private FareService fareService;

    @InjectMocks
    private FareController fareController;

    private FareResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = FareResponse.builder()
                .id("fare-id-1")
                .rideId("ride-uuid-1")
                .distanceInKm(5.0)
                .estimatedFare(350.0)
                .finalFare(0.0)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    @Test
    void estimateFare_ReturnsCreated() {
        FareEstimateRequest request = FareEstimateRequest.builder()
                .rideId("ride-uuid-1")
                .distanceInKm(5.0)
                .build();

        when(fareService.calculateAndSaveEstimate(request)).thenReturn(sampleResponse);

        ResponseEntity<FareResponse> response = fareController.estimateFare(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("fare-id-1", response.getBody().getId());
        assertEquals(350.0, response.getBody().getEstimatedFare());
    }

    @Test
    void getFareById_ReturnsOk() {
        when(fareService.getFareById("fare-id-1")).thenReturn(sampleResponse);

        ResponseEntity<FareResponse> response = fareController.getFareById("fare-id-1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("fare-id-1", response.getBody().getId());
    }

    @Test
    void getFareByRideId_ReturnsOk() {
        when(fareService.getFareByRideId("ride-uuid-1")).thenReturn(sampleResponse);

        ResponseEntity<FareResponse> response = fareController.getFareByRideId("ride-uuid-1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ride-uuid-1", response.getBody().getRideId());
    }

    @Test
    void finalizeFare_ReturnsOk() {
        FinalizeFareRequest request = FinalizeFareRequest.builder()
                .finalFare(380.0)
                .paymentStatus(PaymentStatus.PAID)
                .paymentMethod(com.ridelink.fare_payment_service.model.PaymentMethod.CARD)
                .build();

        FareResponse finalizedResponse = FareResponse.builder()
                .id("fare-id-1")
                .rideId("ride-uuid-1")
                .distanceInKm(5.0)
                .estimatedFare(350.0)
                .finalFare(380.0)
                .paymentStatus(PaymentStatus.PAID)
                .paymentMethod(com.ridelink.fare_payment_service.model.PaymentMethod.CARD)
                .receiptNumber("RCPT-ABC12345")
                .build();

        when(fareService.finalizeFare("fare-id-1", request)).thenReturn(finalizedResponse);

        ResponseEntity<FareResponse> response = fareController.finalizeFare("fare-id-1", request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(380.0, response.getBody().getFinalFare());
        assertEquals(PaymentStatus.PAID, response.getBody().getPaymentStatus());
        assertEquals(com.ridelink.fare_payment_service.model.PaymentMethod.CARD, response.getBody().getPaymentMethod());
        assertEquals("RCPT-ABC12345", response.getBody().getReceiptNumber());
    }
}

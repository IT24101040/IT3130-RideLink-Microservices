package com.ridelink.fare_payment_service.service;

import com.ridelink.fare_payment_service.dto.FareEstimateRequest;
import com.ridelink.fare_payment_service.dto.FareResponse;
import com.ridelink.fare_payment_service.dto.FinalizeFareRequest;
import com.ridelink.fare_payment_service.exception.ResourceNotFoundException;
import com.ridelink.fare_payment_service.model.Fare;
import com.ridelink.fare_payment_service.model.PaymentStatus;
import com.ridelink.fare_payment_service.repository.FareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareServiceTest {

    @Mock
    private FareRepository fareRepository;

    @InjectMocks
    private FareServiceImpl fareService;

    private FareEstimateRequest request;
    private Fare existingFare;

    @BeforeEach
    void setUp() {
        request = FareEstimateRequest.builder()
                .rideId("ride-uuid-12345")
                .distanceInKm(10.0)
                .build();

        existingFare = Fare.builder()
                .id("mongo-fare-id-1")
                .rideId("ride-uuid-12345")
                .distanceInKm(10.0)
                .estimatedFare(600.0)
                .finalFare(0.0)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    @Test
    void calculateAndSaveEstimate_Success() {
        when(fareRepository.save(any(Fare.class))).thenReturn(existingFare);

        FareResponse response = fareService.calculateAndSaveEstimate(request);

        assertNotNull(response);
        assertEquals("mongo-fare-id-1", response.getId());
        assertEquals("ride-uuid-12345", response.getRideId());
        assertEquals(10.0, response.getDistanceInKm());
        assertEquals(600.0, response.getEstimatedFare());
        assertEquals(0.0, response.getFinalFare());
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());

        verify(fareRepository, times(1)).save(argThat(fare ->
                fare.getRideId().equals("ride-uuid-12345") &&
                fare.getEstimatedFare() == 600.0 &&
                fare.getPaymentStatus() == PaymentStatus.PENDING
        ));
    }

    @Test
    void getFareById_Success() {
        when(fareRepository.findById("mongo-fare-id-1")).thenReturn(Optional.of(existingFare));

        FareResponse response = fareService.getFareById("mongo-fare-id-1");

        assertNotNull(response);
        assertEquals("mongo-fare-id-1", response.getId());
        assertEquals("ride-uuid-12345", response.getRideId());
    }

    @Test
    void getFareById_NotFound_ThrowsException() {
        when(fareRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fareService.getFareById("non-existent-id"));
    }

    @Test
    void getFareByRideId_Success() {
        when(fareRepository.findByRideId("ride-uuid-12345")).thenReturn(Optional.of(existingFare));

        FareResponse response = fareService.getFareByRideId("ride-uuid-12345");

        assertNotNull(response);
        assertEquals("ride-uuid-12345", response.getRideId());
        assertEquals("mongo-fare-id-1", response.getId());
    }

    @Test
    void getFareByRideId_NotFound_ThrowsException() {
        when(fareRepository.findByRideId("unknown-ride-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fareService.getFareByRideId("unknown-ride-id"));
    }

    @Test
    void finalizeFare_Success_Paid() {
        FinalizeFareRequest finalizeRequest = FinalizeFareRequest.builder()
                .finalFare(650.0)
                .paymentStatus(PaymentStatus.PAID)
                .paymentMethod(com.ridelink.fare_payment_service.model.PaymentMethod.CARD)
                .build();

        when(fareRepository.findById("mongo-fare-id-1")).thenReturn(Optional.of(existingFare));
        when(fareRepository.save(any(Fare.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FareResponse response = fareService.finalizeFare("mongo-fare-id-1", finalizeRequest);

        assertNotNull(response);
        assertEquals(650.0, response.getFinalFare());
        assertEquals(PaymentStatus.PAID, response.getPaymentStatus());
        assertEquals(com.ridelink.fare_payment_service.model.PaymentMethod.CARD, response.getPaymentMethod());
        assertNotNull(response.getReceiptNumber());
        assertTrue(response.getReceiptNumber().startsWith("RCPT-"));

        verify(fareRepository, times(1)).save(argThat(fare ->
                fare.getFinalFare() == 650.0 &&
                fare.getPaymentStatus() == PaymentStatus.PAID &&
                fare.getPaymentMethod() == com.ridelink.fare_payment_service.model.PaymentMethod.CARD &&
                fare.getReceiptNumber() != null &&
                fare.getReceiptNumber().startsWith("RCPT-")
        ));
    }

    @Test
    void finalizeFare_Success_FailedStatus_NoReceipt() {
        FinalizeFareRequest finalizeRequest = FinalizeFareRequest.builder()
                .finalFare(650.0)
                .paymentStatus(PaymentStatus.FAILED)
                .paymentMethod(com.ridelink.fare_payment_service.model.PaymentMethod.CASH)
                .build();

        when(fareRepository.findById("mongo-fare-id-1")).thenReturn(Optional.of(existingFare));
        when(fareRepository.save(any(Fare.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FareResponse response = fareService.finalizeFare("mongo-fare-id-1", finalizeRequest);

        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, response.getPaymentStatus());
        assertEquals(com.ridelink.fare_payment_service.model.PaymentMethod.CASH, response.getPaymentMethod());
        assertNull(response.getReceiptNumber());
    }

    @Test
    void finalizeFare_PendingStatus_ThrowsIllegalArgumentException() {
        FinalizeFareRequest finalizeRequest = FinalizeFareRequest.builder()
                .finalFare(650.0)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        assertThrows(IllegalArgumentException.class, () -> fareService.finalizeFare("mongo-fare-id-1", finalizeRequest));
    }

    @Test
    void finalizeFare_NotFound_ThrowsException() {
        FinalizeFareRequest finalizeRequest = FinalizeFareRequest.builder()
                .finalFare(650.0)
                .paymentStatus(PaymentStatus.PAID)
                .build();

        when(fareRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fareService.finalizeFare("non-existent-id", finalizeRequest));
    }
}

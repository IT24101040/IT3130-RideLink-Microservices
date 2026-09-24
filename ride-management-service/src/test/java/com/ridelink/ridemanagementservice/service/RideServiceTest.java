package com.ridelink.ridemanagementservice.service;

import com.ridelink.ridemanagementservice.dto.AssignDriverRequest;
import com.ridelink.ridemanagementservice.dto.CreateRideRequest;
import com.ridelink.ridemanagementservice.dto.RideResponse;
import com.ridelink.ridemanagementservice.exception.InvalidStatusTransitionException;
import com.ridelink.ridemanagementservice.exception.RideNotFoundException;
import com.ridelink.ridemanagementservice.model.Ride;
import com.ridelink.ridemanagementservice.model.RideStatus;
import com.ridelink.ridemanagementservice.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RideServiceTest {

    private RideRepository rideRepository;
    private RideService rideService;

    @BeforeEach
    void setUp() {
        rideRepository = Mockito.mock(RideRepository.class);
        rideService = new RideService(rideRepository);
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void createRide_setsStatusToRequested() {
        CreateRideRequest request = new CreateRideRequest();
        request.setCustomerId("cust-1");
        request.setPickupLocation("Kegalle");
        request.setDestinationLocation("Kandy");

        RideResponse response = rideService.createRide(request);

        assertEquals(RideStatus.REQUESTED, response.getStatus());
        assertNotNull(response.getRideId());
    }

    @Test
    void assignDriver_movesRequestedToAssigned() {
        Ride ride = sampleRide(RideStatus.REQUESTED);
        when(rideRepository.findById("ride-1")).thenReturn(Optional.of(ride));

        AssignDriverRequest req = new AssignDriverRequest();
        req.setDriverId("driver-9");

        RideResponse response = rideService.assignDriver("ride-1", req);

        assertEquals(RideStatus.ASSIGNED, response.getStatus());
        assertEquals("driver-9", response.getDriverId());
    }

    @Test
    void updateStatus_rejectsInvalidTransition() {
        Ride ride = sampleRide(RideStatus.COMPLETED);
        when(rideRepository.findById("ride-1")).thenReturn(Optional.of(ride));

        assertThrows(InvalidStatusTransitionException.class,
                () -> rideService.updateStatus("ride-1", RideStatus.ACCEPTED));
    }

    @Test
    void getRide_throwsWhenNotFound() {
        when(rideRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class, () -> rideService.getRide("missing"));
    }

    private Ride sampleRide(RideStatus status) {
        Instant now = Instant.now();
        return Ride.builder()
                .id("ride-1")
                .customerId("cust-1")
                .pickupLocation("Kegalle")
                .destinationLocation("Kandy")
                .status(status)
                .requestedAt(now)
                .updatedAt(now)
                .build();
    }
}

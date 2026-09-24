package com.ridelink.ridemanagementservice.service;

import com.ridelink.ridemanagementservice.dto.AssignDriverRequest;
import com.ridelink.ridemanagementservice.dto.CreateRideRequest;
import com.ridelink.ridemanagementservice.dto.RideResponse;
import com.ridelink.ridemanagementservice.exception.InvalidStatusTransitionException;
import com.ridelink.ridemanagementservice.exception.RideNotFoundException;
import com.ridelink.ridemanagementservice.model.Ride;
import com.ridelink.ridemanagementservice.model.RideStatus;
import com.ridelink.ridemanagementservice.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    // private final DriverServiceClient driverServiceClient; // wired in Step 6 (interservice communication)
    // private final FareServiceClient fareServiceClient;      // wired in Step 6

    public RideResponse createRide(CreateRideRequest request) {
        Instant now = Instant.now();
        Ride ride = Ride.builder()
                .id(UUID.randomUUID().toString())
                .customerId(request.getCustomerId())
                .pickupLocation(request.getPickupLocation())
                .destinationLocation(request.getDestinationLocation())
                .estimatedFare(request.getEstimatedFare())
                .status(RideStatus.REQUESTED)
                .requestedAt(now)
                .updatedAt(now)
                .build();

        return RideResponse.from(rideRepository.save(ride));
    }

    public RideResponse getRide(String rideId) {
        return RideResponse.from(findRideOrThrow(rideId));
    }

    public List<RideResponse> getRidesByCustomer(String customerId) {
        return rideRepository.findByCustomerId(customerId).stream()
                .map(RideResponse::from)
                .toList();
    }

    public List<RideResponse> getRidesByDriver(String driverId) {
        return rideRepository.findByDriverId(driverId).stream()
                .map(RideResponse::from)
                .toList();
    }

    public RideResponse assignDriver(String rideId, AssignDriverRequest request) {
        Ride ride = findRideOrThrow(rideId);
        transition(ride, RideStatus.ASSIGNED);
        ride.setDriverId(request.getDriverId());
        ride.setUpdatedAt(Instant.now());
        return RideResponse.from(rideRepository.save(ride));
    }

    public RideResponse updateStatus(String rideId, RideStatus newStatus) {
        Ride ride = findRideOrThrow(rideId);
        transition(ride, newStatus);
        ride.setUpdatedAt(Instant.now());
        return RideResponse.from(rideRepository.save(ride));
    }

    private void transition(Ride ride, RideStatus target) {
        if (!ride.getStatus().canTransitionTo(target)) {
            throw new InvalidStatusTransitionException(ride.getStatus(), target);
        }
        ride.setStatus(target);
    }

    private Ride findRideOrThrow(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));
    }
}

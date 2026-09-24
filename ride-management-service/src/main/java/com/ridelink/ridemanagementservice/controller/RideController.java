package com.ridelink.ridemanagementservice.controller;

import com.ridelink.ridemanagementservice.dto.AssignDriverRequest;
import com.ridelink.ridemanagementservice.dto.CreateRideRequest;
import com.ridelink.ridemanagementservice.dto.RideResponse;
import com.ridelink.ridemanagementservice.dto.UpdateStatusRequest;
import com.ridelink.ridemanagementservice.model.RideStatus;
import com.ridelink.ridemanagementservice.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
@Tag(name = "Ride Management", description = "Ride request, assignment and lifecycle operations")
public class RideController {

    private final RideService rideService;

    @Operation(summary = "Create a new ride request (CUSTOMER)")
    @PostMapping
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody CreateRideRequest request) {
        RideResponse created = rideService.createRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Get a ride by its id")
    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRide(rideId));
    }

    @Operation(summary = "List rides for a customer or a driver (provide exactly one query param)")
    @GetMapping
    public ResponseEntity<List<RideResponse>> listRides(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String driverId) {

        if (customerId != null) {
            return ResponseEntity.ok(rideService.getRidesByCustomer(customerId));
        }
        if (driverId != null) {
            return ResponseEntity.ok(rideService.getRidesByDriver(driverId));
        }
        throw new IllegalArgumentException("Provide either customerId or driverId as a query parameter");
    }

    @Operation(summary = "Assign a driver to a ride (REQUESTED -> ASSIGNED)")
    @PatchMapping("/{rideId}/assign")
    public ResponseEntity<RideResponse> assignDriver(@PathVariable String rideId,
                                                       @Valid @RequestBody AssignDriverRequest request) {
        return ResponseEntity.ok(rideService.assignDriver(rideId, request));
    }

    @Operation(summary = "Update ride status following the defined lifecycle")
    @PatchMapping("/{rideId}/status")
    public ResponseEntity<RideResponse> updateStatus(@PathVariable String rideId,
                                                       @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(rideService.updateStatus(rideId, request.getStatus()));
    }

    @Operation(summary = "Cancel a ride (shortcut for status=CANCELLED)")
    @DeleteMapping("/{rideId}")
    public ResponseEntity<RideResponse> cancelRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.updateStatus(rideId, RideStatus.CANCELLED));
    }
}

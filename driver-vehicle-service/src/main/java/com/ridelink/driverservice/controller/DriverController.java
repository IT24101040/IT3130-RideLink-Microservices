package com.ridelink.driverservice.controller;

import com.ridelink.driverservice.dto.*;
import com.ridelink.driverservice.model.VehicleType;
import com.ridelink.driverservice.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers & Vehicles")
@SecurityRequirement(name = "bearerAuth")
public class DriverController {

    private final DriverService driverService;

    @Operation(summary = "Register the logged-in driver's profile and vehicle (DRIVER)")
    @PostMapping
    public ResponseEntity<DriverResponse> register(Principal principal,
                                                   @Valid @RequestBody DriverRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(driverService.registerDriver(principal.getName(), request));
    }

    @Operation(summary = "Get my driver profile (DRIVER)")
    @GetMapping("/me")
    public DriverResponse me(Principal principal) {
        return driverService.getMyProfile(principal.getName());
    }

    @Operation(summary = "Update my profile details (DRIVER)")
    @PutMapping("/me")
    public DriverResponse updateProfile(Principal principal, @Valid @RequestBody DriverUpdateRequest request) {
        return driverService.updateProfile(principal.getName(), request);
    }

    @Operation(summary = "Update my vehicle details (DRIVER)")
    @PutMapping("/me/vehicle")
    public DriverResponse updateVehicle(Principal principal, @Valid @RequestBody VehicleRequest request) {
        return driverService.updateVehicle(principal.getName(), request);
    }

    @Operation(summary = "Set my availability: AVAILABLE / OFFLINE / ON_RIDE (DRIVER)")
    @PatchMapping("/me/availability")
    public DriverResponse updateAvailability(Principal principal, @Valid @RequestBody AvailabilityRequest request) {
        return driverService.updateAvailability(principal.getName(), request);
    }

    @Operation(summary = "Update my simulated current location / service area (DRIVER)")
    @PutMapping("/me/location")
    public DriverResponse updateLocation(Principal principal, @Valid @RequestBody LocationRequest request) {
        return driverService.updateLocation(principal.getName(), request);
    }

    @Operation(summary = "Find eligible available drivers (any authenticated user; used by Ride Service)")
    @GetMapping("/available")
    public List<DriverPublicResponse> available(@RequestParam(required = false) String city,
                                                @RequestParam(required = false) VehicleType vehicleType,
                                                @RequestParam(required = false) Double lat,
                                                @RequestParam(required = false) Double lng,
                                                @RequestParam(required = false) Double radiusKm) {
        return driverService.findAvailableDrivers(city, vehicleType, lat, lng, radiusKm);
    }

    @Operation(summary = "Get a driver's public details by driverId (any authenticated user)")
    @GetMapping("/{driverId}")
    public DriverPublicResponse getById(@PathVariable String driverId) {
        return driverService.getDriverById(driverId);
    }

    @Operation(summary = "List all drivers with full details (ADMIN)")
    @GetMapping
    public List<DriverResponse> all() {
        return driverService.getAllDrivers();
    }
}
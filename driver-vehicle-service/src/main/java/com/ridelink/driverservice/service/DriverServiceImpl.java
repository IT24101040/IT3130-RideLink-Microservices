package com.ridelink.driverservice.service;

import com.ridelink.driverservice.dto.*;
import com.ridelink.driverservice.exception.*;
import com.ridelink.driverservice.mapper.DriverMapper;
import com.ridelink.driverservice.model.*;
import com.ridelink.driverservice.repository.DriverRepository;
import com.ridelink.driverservice.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper mapper;

    private record Candidate(Driver driver, Double distanceKm) {}

    @Override
    public DriverResponse registerDriver(String userId, DriverRegistrationRequest req) {
        if (driverRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException("A driver profile already exists for this account");
        }
        String license = req.licenseNumber().trim().toUpperCase();
        if (driverRepository.existsByLicenseNumber(license)) {
            throw new DuplicateResourceException("Licence number is already registered");
        }
        Vehicle vehicle = mapper.toVehicle(req.vehicle());
        if (driverRepository.existsByVehiclePlateNumber(vehicle.getPlateNumber())) {
            throw new DuplicateResourceException("Vehicle plate number is already registered");
        }
        Instant now = Instant.now();
        Driver driver = Driver.builder()
                .driverId(UUID.randomUUID().toString())
                .userId(userId)
                .fullName(req.fullName().trim())
                .phoneNumber(req.phoneNumber())
                .licenseNumber(license)
                .city(req.city().trim())
                .vehicle(vehicle)
                .availabilityStatus(AvailabilityStatus.OFFLINE)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return mapper.toResponse(driverRepository.save(driver));
    }

    @Override
    public DriverResponse getMyProfile(String userId) {
        return mapper.toResponse(findByUserIdOrThrow(userId));
    }

    @Override
    public DriverPublicResponse getDriverById(String driverId) {
        Driver driver = driverRepository.findByDriverId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + driverId));
        return mapper.toPublicResponse(driver, null);
    }

    @Override
    public DriverResponse updateProfile(String userId, DriverUpdateRequest req) {
        Driver driver = findByUserIdOrThrow(userId);
        driver.setFullName(req.fullName().trim());
        driver.setPhoneNumber(req.phoneNumber());
        driver.setCity(req.city().trim());
        return save(driver);
    }

    @Override
    public DriverResponse updateVehicle(String userId, VehicleRequest req) {
        Driver driver = findByUserIdOrThrow(userId);
        Vehicle updated = mapper.toVehicle(req);
        boolean plateChanged = !updated.getPlateNumber().equals(driver.getVehicle().getPlateNumber());
        if (plateChanged && driverRepository.existsByVehiclePlateNumber(updated.getPlateNumber())) {
            throw new DuplicateResourceException("Vehicle plate number is already registered");
        }
        driver.setVehicle(updated);
        return save(driver);
    }

    @Override
    public DriverResponse updateAvailability(String userId, AvailabilityRequest req) {
        Driver driver = findByUserIdOrThrow(userId);
        if (req.status() == AvailabilityStatus.AVAILABLE && driver.getCurrentLocation() == null) {
            throw new InvalidRequestException("Set your current location before going AVAILABLE");
        }
        driver.setAvailabilityStatus(req.status());
        return save(driver);
    }

    @Override
    public DriverResponse updateLocation(String userId, LocationRequest req) {
        Driver driver = findByUserIdOrThrow(userId);
        driver.setCurrentLocation(Location.builder()
                .latitude(req.latitude())
                .longitude(req.longitude())
                .updatedAt(Instant.now())
                .build());
        if (req.city() != null && !req.city().isBlank()) {
            driver.setCity(req.city().trim());
        }
        return save(driver);
    }

    @Override
    public List<DriverPublicResponse> findAvailableDrivers(String city, VehicleType vehicleType,
                                                           Double lat, Double lng, Double radiusKm) {
        if ((lat == null) != (lng == null)) {
            throw new InvalidRequestException("lat and lng must be provided together");
        }
        boolean hasPoint = lat != null;
        if (hasPoint && (lat < -90 || lat > 90 || lng < -180 || lng > 180)) {
            throw new InvalidRequestException("lat or lng is out of range");
        }
        if (radiusKm != null && (radiusKm <= 0 || !hasPoint)) {
            throw new InvalidRequestException("radiusKm must be positive and requires lat and lng");
        }

        List<Driver> drivers = (city == null || city.isBlank())
                ? driverRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE)
                : driverRepository.findByAvailabilityStatusAndCityIgnoreCase(AvailabilityStatus.AVAILABLE, city.trim());

        return drivers.stream()
                .filter(d -> vehicleType == null || d.getVehicle().getType() == vehicleType)
                .map(d -> new Candidate(d, distanceKm(d, lat, lng)))
                .filter(c -> radiusKm == null || (c.distanceKm() != null && c.distanceKm() <= radiusKm))
                .sorted(Comparator.comparing(Candidate::distanceKm, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(c -> mapper.toPublicResponse(c.driver(), c.distanceKm()))
                .toList();
    }

    @Override
    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    // ---- helpers ----
    private Driver findByUserIdOrThrow(String userId) {
        return driverRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found for this account"));
    }

    private DriverResponse save(Driver driver) {
        driver.setUpdatedAt(Instant.now());
        return mapper.toResponse(driverRepository.save(driver));
    }

    private Double distanceKm(Driver d, Double lat, Double lng) {
        if (lat == null || d.getCurrentLocation() == null) return null;
        double km = GeoUtils.haversineKm(lat, lng,
                d.getCurrentLocation().getLatitude(), d.getCurrentLocation().getLongitude());
        return Math.round(km * 100.0) / 100.0;
    }
}
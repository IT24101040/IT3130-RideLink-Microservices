package com.ridelink.driverservice.service;

import com.ridelink.driverservice.dto.*;
import com.ridelink.driverservice.model.VehicleType;
import java.util.List;

public interface DriverService {
    DriverResponse registerDriver(String userId, DriverRegistrationRequest request);
    DriverResponse getMyProfile(String userId);
    DriverPublicResponse getDriverById(String driverId);
    DriverResponse updateProfile(String userId, DriverUpdateRequest request);
    DriverResponse updateVehicle(String userId, VehicleRequest request);
    DriverResponse updateAvailability(String userId, AvailabilityRequest request);
    DriverResponse updateLocation(String userId, LocationRequest request);
    List<DriverPublicResponse> findAvailableDrivers(String city, VehicleType vehicleType,
                                                    Double lat, Double lng, Double radiusKm);
    List<DriverResponse> getAllDrivers();
}
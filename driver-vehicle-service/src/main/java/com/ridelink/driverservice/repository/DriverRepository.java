package com.ridelink.driverservice.repository;

import com.ridelink.driverservice.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;

public interface DriverRepository extends MongoRepository<Driver, String> {
    Optional<Driver> findByDriverId(String driverId);
    Optional<Driver> findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByVehiclePlateNumber(String plateNumber);
    List<Driver> findByAvailabilityStatus(AvailabilityStatus status);
    List<Driver> findByAvailabilityStatusAndCityIgnoreCase(AvailabilityStatus status, String city);
}
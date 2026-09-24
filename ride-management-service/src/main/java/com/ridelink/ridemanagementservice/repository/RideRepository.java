package com.ridelink.ridemanagementservice.repository;

import com.ridelink.ridemanagementservice.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RideRepository extends MongoRepository<Ride, String> {
    List<Ride> findByCustomerId(String customerId);
    List<Ride> findByDriverId(String driverId);
}

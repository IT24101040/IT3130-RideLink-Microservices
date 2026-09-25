package com.ridelink.driverservice.mapper;

import com.ridelink.driverservice.dto.*;
import com.ridelink.driverservice.model.*;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public Vehicle toVehicle(VehicleRequest r) {
        return Vehicle.builder()
                .plateNumber(r.plateNumber().trim().toUpperCase())
                .make(r.make().trim())
                .model(r.model().trim())
                .color(r.color())
                .year(r.year())
                .type(r.type())
                .seatCapacity(r.seatCapacity())
                .build();
    }

    public DriverResponse toResponse(Driver d) {
        return new DriverResponse(d.getDriverId(), d.getUserId(), d.getFullName(), d.getPhoneNumber(),
                d.getLicenseNumber(), d.getCity(), toVehicleResponse(d.getVehicle()),
                d.getAvailabilityStatus(), toLocationResponse(d.getCurrentLocation()),
                d.getCreatedAt(), d.getUpdatedAt());
    }

    public DriverPublicResponse toPublicResponse(Driver d, Double distanceKm) {
        return new DriverPublicResponse(d.getDriverId(), d.getFullName(), d.getCity(),
                toVehicleResponse(d.getVehicle()), d.getAvailabilityStatus(),
                toLocationResponse(d.getCurrentLocation()), distanceKm);
    }

    private VehicleResponse toVehicleResponse(Vehicle v) {
        if (v == null) return null;
        return new VehicleResponse(v.getPlateNumber(), v.getMake(), v.getModel(), v.getColor(),
                v.getYear(), v.getType(), v.getSeatCapacity());
    }

    private LocationResponse toLocationResponse(Location l) {
        return l == null ? null : new LocationResponse(l.getLatitude(), l.getLongitude(), l.getUpdatedAt());
    }
}
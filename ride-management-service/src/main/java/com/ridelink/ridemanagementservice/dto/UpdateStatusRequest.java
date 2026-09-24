package com.ridelink.ridemanagementservice.dto;

import com.ridelink.ridemanagementservice.model.RideStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull(message = "status is required")
    private RideStatus status;
}

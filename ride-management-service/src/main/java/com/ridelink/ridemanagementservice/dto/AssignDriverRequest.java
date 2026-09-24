package com.ridelink.ridemanagementservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignDriverRequest {

    @NotBlank(message = "driverId is required")
    private String driverId;
}

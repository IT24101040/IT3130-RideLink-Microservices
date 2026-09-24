package com.ridelink.ridemanagementservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Matches the error shape agreed in docs/architecture.md (section 6) — keep this
 * identical across all four services.
 */
@Data
@Builder
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}

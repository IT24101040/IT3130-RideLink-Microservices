package com.ridelink.fare_payment_service.controller;

import com.ridelink.fare_payment_service.dto.FareEstimateRequest;
import com.ridelink.fare_payment_service.dto.FareResponse;
import com.ridelink.fare_payment_service.dto.FinalizeFareRequest;
import com.ridelink.fare_payment_service.service.FareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fares")
@RequiredArgsConstructor
public class FareController {

    private final FareService fareService;

    @PostMapping("/estimate")
    public ResponseEntity<FareResponse> estimateFare(@Valid @RequestBody FareEstimateRequest request) {
        FareResponse response = fareService.calculateAndSaveEstimate(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FareResponse> getFareById(@PathVariable String id) {
        FareResponse response = fareService.getFareById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<FareResponse> getFareByRideId(@PathVariable String rideId) {
        FareResponse response = fareService.getFareByRideId(rideId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/finalize")
    public ResponseEntity<FareResponse> finalizeFare(
            @PathVariable String id,
            @Valid @RequestBody FinalizeFareRequest request) {
        FareResponse response = fareService.finalizeFare(id, request);
        return ResponseEntity.ok(response);
    }
}


package com.ridelink.driverservice.service;

import com.ridelink.driverservice.dto.*;
import com.ridelink.driverservice.exception.*;
import com.ridelink.driverservice.mapper.DriverMapper;
import com.ridelink.driverservice.model.*;
import com.ridelink.driverservice.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock private DriverRepository driverRepository;
    private DriverServiceImpl driverService;

    @BeforeEach
    void setUp() {
        driverService = new DriverServiceImpl(driverRepository, new DriverMapper());
    }

    private DriverRegistrationRequest validRequest() {
        return new DriverRegistrationRequest("Nimal Perera", "0771234567", "B1234567", "Kandy",
                new VehicleRequest("CAB-1234", "Toyota", "Axio", "White", 2018, VehicleType.CAR, 4));
    }

    private Driver driver(String driverId, double lat, double lng) {
        return Driver.builder()
                .driverId(driverId).userId("u-" + driverId).fullName("Driver " + driverId).city("Kandy")
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .vehicle(Vehicle.builder().plateNumber("P-" + driverId).type(VehicleType.CAR).build())
                .currentLocation(Location.builder().latitude(lat).longitude(lng).updatedAt(Instant.now()).build())
                .build();
    }

    @Test
    void registerDriver_validRequest_createsOfflineDriverWithUuid() {
        when(driverRepository.existsByUserId("u1")).thenReturn(false);
        when(driverRepository.existsByLicenseNumber("B1234567")).thenReturn(false);
        when(driverRepository.existsByVehiclePlateNumber("CAB-1234")).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenAnswer(inv -> inv.getArgument(0));

        DriverResponse res = driverService.registerDriver("u1", validRequest());

        assertThat(res.driverId()).isNotBlank();
        assertThat(res.userId()).isEqualTo("u1");
        assertThat(res.availabilityStatus()).isEqualTo(AvailabilityStatus.OFFLINE);
    }

    @Test
    void registerDriver_existingProfile_throwsDuplicate() {
        when(driverRepository.existsByUserId("u1")).thenReturn(true);

        assertThatThrownBy(() -> driverService.registerDriver("u1", validRequest()))
                .isInstanceOf(DuplicateResourceException.class);
        verify(driverRepository, never()).save(any());
    }

    @Test
    void registerDriver_duplicatePlate_throwsDuplicate() {
        when(driverRepository.existsByUserId("u1")).thenReturn(false);
        when(driverRepository.existsByLicenseNumber("B1234567")).thenReturn(false);
        when(driverRepository.existsByVehiclePlateNumber("CAB-1234")).thenReturn(true);

        assertThatThrownBy(() -> driverService.registerDriver("u1", validRequest()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("plate");
    }

    @Test
    void getMyProfile_unknownUser_throwsNotFound() {
        when(driverRepository.findByUserId("nobody")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.getMyProfile("nobody"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateAvailability_availableWithoutLocation_throwsInvalidRequest() {
        Driver d = Driver.builder().driverId("d1").userId("u1")
                .availabilityStatus(AvailabilityStatus.OFFLINE).build();
        when(driverRepository.findByUserId("u1")).thenReturn(Optional.of(d));

        assertThatThrownBy(() -> driverService.updateAvailability("u1", new AvailabilityRequest(AvailabilityStatus.AVAILABLE)))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("location");
        verify(driverRepository, never()).save(any());
    }

    @Test
    void updateAvailability_withLocation_setsAvailable() {
        Driver d = driver("d1", 7.29, 80.63);
        d.setAvailabilityStatus(AvailabilityStatus.OFFLINE);
        d.setUserId("u1");
        when(driverRepository.findByUserId("u1")).thenReturn(Optional.of(d));
        when(driverRepository.save(any(Driver.class))).thenAnswer(inv -> inv.getArgument(0));

        DriverResponse res = driverService.updateAvailability("u1", new AvailabilityRequest(AvailabilityStatus.AVAILABLE));

        assertThat(res.availabilityStatus()).isEqualTo(AvailabilityStatus.AVAILABLE);
    }

    @Test
    void findAvailableDrivers_withRadius_filtersAndSortsByDistance() {
        Driver near = driver("near", 7.2906, 80.6337);
        Driver mid = driver("mid", 7.3300, 80.6500);
        Driver far = driver("far", 7.5000, 80.9000);
        when(driverRepository.findByAvailabilityStatusAndCityIgnoreCase(AvailabilityStatus.AVAILABLE, "Kandy"))
                .thenReturn(List.of(far, mid, near));

        List<DriverPublicResponse> result =
                driverService.findAvailableDrivers("Kandy", null, 7.2900, 80.6300, 10.0);

        assertThat(result).extracting(DriverPublicResponse::driverId).containsExactly("near", "mid");
        assertThat(result.get(0).distanceKm()).isLessThan(result.get(1).distanceKm());
    }

    @Test
    void findAvailableDrivers_noMatches_returnsEmptyList() {
        when(driverRepository.findByAvailabilityStatusAndCityIgnoreCase(AvailabilityStatus.AVAILABLE, "Galle"))
                .thenReturn(List.of());

        assertThat(driverService.findAvailableDrivers("Galle", null, null, null, null)).isEmpty();
    }

    @Test
    void findAvailableDrivers_onlyLatProvided_throwsInvalidRequest() {
        assertThatThrownBy(() -> driverService.findAvailableDrivers("Kandy", null, 7.29, null, null))
                .isInstanceOf(InvalidRequestException.class);
        verifyNoInteractions(driverRepository);
    }
}
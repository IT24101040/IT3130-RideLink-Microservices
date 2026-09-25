package com.ridelink.driverservice.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GeoUtilsTest {

    @Test
    void samePoint_returnsZero() {
        assertThat(GeoUtils.haversineKm(7.29, 80.63, 7.29, 80.63)).isZero();
    }

    @Test
    void colomboToKandy_isRoughly94Km() {
        double km = GeoUtils.haversineKm(6.9271, 79.8612, 7.2906, 80.6337);
        assertThat(km).isBetween(92.0, 97.0);
    }
}
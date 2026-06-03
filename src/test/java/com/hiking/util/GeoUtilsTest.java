package com.hiking.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoUtilsTest {

    @Test
    void distanceKm_samePoint_isZero() {
        assertEquals(0.0, GeoUtils.distanceKm(42.0, 23.0, 42.0, 23.0), 1e-9);
    }

    @Test
    void distanceKm_isSymmetric() {
        double a = GeoUtils.distanceKm(42.0, 23.0, 42.7, 23.3);
        double b = GeoUtils.distanceKm(42.7, 23.3, 42.0, 23.0);
        assertEquals(a, b, 1e-9);
    }

    @Test
    void distanceKm_knownDistance_oneDegreeLatitude() {
        // One degree of latitude is ~111.19 km on a sphere of radius 6371 km.
        double d = GeoUtils.distanceKm(0.0, 0.0, 1.0, 0.0);
        assertEquals(111.19, d, 0.5);
    }

    @Test
    void distanceKm_sofiaToPlovdiv_isApproximatelyCorrect() {
        // Sofia (42.6977, 23.3219) to Plovdiv (42.1354, 24.7453) ~= 130 km great-circle.
        double d = GeoUtils.distanceKm(42.6977, 23.3219, 42.1354, 24.7453);
        assertEquals(130.0, d, 10.0);
    }

    @Test
    void distanceKm_isAlwaysNonNegative() {
        assertTrue(GeoUtils.distanceKm(-10.0, -20.0, 30.0, 40.0) >= 0);
    }
}

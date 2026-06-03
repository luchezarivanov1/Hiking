package com.hiking.service;

import com.hiking.dto.RouteMapDTO;
import com.hiking.entity.RouteWaypoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.hiking.repository.RouteWaypointRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapServiceTest {

    @Mock
    private RouteWaypointRepository repo;

    @InjectMocks
    private MapService mapService;

    private RouteWaypoint waypoint(double lat, double lon) {
        RouteWaypoint wp = new RouteWaypoint();
        wp.setLatitude(lat);
        wp.setLongitude(lon);
        return wp;
    }

    @Test
    void getPolyline_mapsWaypointsToCoordinatePairs() {
        when(repo.findByHikingRouteIdOrderByOrderIndex(7L))
                .thenReturn(List.of(waypoint(42.0, 23.0), waypoint(42.5, 23.5)));

        RouteMapDTO dto = mapService.getPolyline(7L);

        assertEquals(7L, dto.getRouteId());
        assertEquals(2, dto.getCoordinates().size());
        assertEquals(List.of(42.0, 23.0), dto.getCoordinates().get(0));
        assertEquals(List.of(42.5, 23.5), dto.getCoordinates().get(1));
    }

    @Test
    void getPolyline_emptyRoute_returnsEmptyCoordinates() {
        when(repo.findByHikingRouteIdOrderByOrderIndex(1L)).thenReturn(List.of());

        RouteMapDTO dto = mapService.getPolyline(1L);

        assertEquals(1L, dto.getRouteId());
        assertTrue(dto.getCoordinates().isEmpty());
    }
}

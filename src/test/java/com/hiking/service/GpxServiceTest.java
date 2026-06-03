package com.hiking.service;

import com.hiking.entity.HikingRoute;
import com.hiking.entity.RouteWaypoint;
import com.hiking.exception.BadRequestException;
import com.hiking.exception.ResourceNotFoundException;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.RouteWaypointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GpxServiceTest {

    @Mock
    private HikingRouteRepository routeRepo;
    @Mock
    private RouteWaypointRepository waypointRepo;

    @InjectMocks
    private GpxService gpxService;

    private HikingRoute route;

    @BeforeEach
    void setUp() {
        route = new HikingRoute();
        route.setId(5L);
    }

    private MultipartFile gpxFile(String xml) {
        return new MockMultipartFile("file", "route.gpx", "application/gpx+xml", xml.getBytes());
    }

    private static final String GPX_WITH_TRACK = """
            <?xml version="1.0" encoding="UTF-8"?>
            <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
              <trk><trkseg>
                <trkpt lat="42.0000" lon="23.0000"></trkpt>
                <trkpt lat="42.0100" lon="23.0000"></trkpt>
                <trkpt lat="42.0200" lon="23.0000"></trkpt>
              </trkseg></trk>
            </gpx>
            """;

    private static final String GPX_EMPTY = """
            <?xml version="1.0" encoding="UTF-8"?>
            <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
            </gpx>
            """;

    @Test
    void importGpx_routeNotFound_throws() {
        when(routeRepo.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> gpxService.importGpxIntoRoute(5L, gpxFile(GPX_WITH_TRACK)));
    }

    @Test
    void importGpx_withTrackPoints_persistsWaypointsAndComputesDistance() {
        when(routeRepo.findById(5L)).thenReturn(Optional.of(route));
        when(routeRepo.save(route)).thenReturn(route);

        HikingRoute result = gpxService.importGpxIntoRoute(5L, gpxFile(GPX_WITH_TRACK));

        // existing waypoints cleared first
        verify(waypointRepo).deleteByHikingRouteId(5L);
        verify(waypointRepo).flush();

        ArgumentCaptor<List<RouteWaypoint>> captor = ArgumentCaptor.forClass(List.class);
        verify(waypointRepo).saveAll(captor.capture());
        List<RouteWaypoint> saved = captor.getValue();
        assertEquals(3, saved.size());
        assertEquals(0, saved.get(0).getOrderIndex());
        assertEquals(2, saved.get(2).getOrderIndex());

        // ~2.2 km total for two 0.01-degree latitude steps; duration derived at 4 km/h
        assertNotNull(result.getDistanceKm());
        assertTrue(result.getDistanceKm() > 2.0 && result.getDistanceKm() < 2.5,
                "unexpected distance: " + result.getDistanceKm());
        assertEquals((int) ((result.getDistanceKm() / 4.0) * 60), result.getDurationMin());
    }

    @Test
    void importGpx_noPoints_throwsBadRequest() {
        when(routeRepo.findById(5L)).thenReturn(Optional.of(route));

        assertThrows(BadRequestException.class,
                () -> gpxService.importGpxIntoRoute(5L, gpxFile(GPX_EMPTY)));

        verify(waypointRepo, never()).saveAll(anyList());
        verify(routeRepo, never()).save(any());
    }

    @Test
    void importGpx_malformedFile_throwsRuntime() {
        when(routeRepo.findById(5L)).thenReturn(Optional.of(route));

        assertThrows(RuntimeException.class,
                () -> gpxService.importGpxIntoRoute(5L, gpxFile("not xml at all")));
    }
}

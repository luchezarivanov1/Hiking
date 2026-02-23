package com.hiking.service;

import com.hiking.dto.RouteMapDTO;
import com.hiking.repository.RouteWaypointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {

    private final RouteWaypointRepository repo;

    public RouteMapDTO getPolyline(Long routeId) {

        var waypoints = repo.findByHikingRouteIdOrderByOrderIndex(routeId);

        RouteMapDTO dto = new RouteMapDTO();
        dto.setRouteId(routeId);

        List<List<Double>> coords = waypoints.stream()
                .map(wp -> List.of(wp.getLatitude(), wp.getLongitude()))
                .toList();

        dto.setCoordinates(coords);

        return dto;
    }
}

package com.hiking.service;

import com.hiking.entity.HikingRoute;
import com.hiking.entity.RouteWaypoint;
import com.hiking.exception.ResourceNotFoundException;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.RouteWaypointRepository;
import com.hiking.util.GeoUtils;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GpxService {

    private final HikingRouteRepository routeRepo;
    private final RouteWaypointRepository waypointRepo;

    @Transactional
    public HikingRoute importGpxIntoRoute(Long routeId, MultipartFile file) {
        HikingRoute route = routeRepo.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("route-", ".gpx");
            file.transferTo(tempFile.toFile());

            GPX gpx = GPX.read(tempFile);

            waypointRepo.deleteByHikingRouteId(route.getId());
            waypointRepo.flush();

            List<WayPoint> points = new ArrayList<>();
            for (var track : gpx.getTracks()) {
                for (var segment : track.getSegments()) {
                    points.addAll(segment.getPoints());
                }
            }
            if (points.isEmpty()) {
                for (var rte : gpx.getRoutes()) {
                    points.addAll(rte.getPoints());
                }
            }
            if (points.isEmpty()) {
                points.addAll(gpx.getWayPoints());
            }
            if (points.isEmpty()) {
                throw new com.hiking.exception.BadRequestException(
                        "GPX file contains no points (tracks, routes, or waypoints)");
            }

            List<RouteWaypoint> waypoints = new ArrayList<>();
            double totalDistance = 0;
            Double prevLat = null;
            Double prevLon = null;
            int index = 0;

            for (WayPoint point : points) {
                double lat = point.getLatitude().doubleValue();
                double lon = point.getLongitude().doubleValue();

                if (prevLat != null) {
                    totalDistance += GeoUtils.distanceKm(prevLat, prevLon, lat, lon);
                }
                prevLat = lat;
                prevLon = lon;

                RouteWaypoint wp = new RouteWaypoint();
                wp.setLatitude(lat);
                wp.setLongitude(lon);
                wp.setOrderIndex(index++);
                wp.setHikingRoute(route);
                waypoints.add(wp);
            }

            waypointRepo.saveAll(waypoints);

            route.setDistanceKm(Math.round(totalDistance * 100.0) / 100.0);
            route.setDurationMin((int) ((route.getDistanceKm() / 4.0) * 60));
            return routeRepo.save(route);

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GPX file", e);
        } finally {
            if (tempFile != null) {
                try { Files.deleteIfExists(tempFile); } catch (Exception ignored) {}
            }
        }
    }
}

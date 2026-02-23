package com.hiking.service;

import com.hiking.entity.HikingRoute;
import com.hiking.entity.RouteWaypoint;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.RouteWaypointRepository;
import com.hiking.util.GeoUtils;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public HikingRoute importGpx(MultipartFile file, String routeName) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("route-", ".gpx");
            file.transferTo(tempFile.toFile());

            GPX gpx = GPX.read(tempFile);

            HikingRoute route = new HikingRoute();
            route.setName(routeName);
            route.setDifficulty("UNKNOWN");

            route = routeRepo.save(route);

            List<RouteWaypoint> waypoints = new ArrayList<>();

            double totalDistance = 0;
            Double prevLat = null;
            Double prevLon = null;

            int index = 0;

            for (var track : gpx.getTracks()) {
                for (var segment : track.getSegments()) {
                    for (WayPoint point : segment.getPoints()) {

                        double lat = point.getLatitude().doubleValue();
                        double lon = point.getLongitude().doubleValue();

                        // Calculate distance
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
                }
            }

            waypointRepo.saveAll(waypoints);

            // Save calculated distance
            route.setDistanceKm(Math.round(totalDistance * 100.0) / 100.0);

            // Estimate duration automatically (avg hiking speed 4 km/h)
            route.setDurationMin((int) ((route.getDistanceKm() / 4.0) * 60));

            return routeRepo.save(route);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GPX file", e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (Exception ignored) {}
            }
        }
    }
}

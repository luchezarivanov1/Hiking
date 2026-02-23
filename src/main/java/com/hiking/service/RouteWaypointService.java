package com.hiking.service;

import com.hiking.entity.RouteWaypoint;
import com.hiking.repository.RouteWaypointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteWaypointService {

    private final RouteWaypointRepository repo;

    public List<RouteWaypoint> getAll() {
        return repo.findAll();
    }

    public RouteWaypoint create(RouteWaypoint wp) {
        return repo.save(wp);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

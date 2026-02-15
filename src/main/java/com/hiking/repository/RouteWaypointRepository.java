package com.hiking.repository;

import com.hiking.entity.RouteWaypoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteWaypointRepository extends JpaRepository<RouteWaypoint, Long> {}

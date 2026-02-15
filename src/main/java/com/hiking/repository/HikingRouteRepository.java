package com.hiking.repository;

import com.hiking.entity.HikingRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HikingRouteRepository extends JpaRepository<HikingRoute, Long> { }

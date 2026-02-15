package com.hiking.repository;

import com.hiking.entity.RoutePhoto;
import com.hiking.entity.HikingRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutePhotoRepository extends JpaRepository<RoutePhoto, Long> {
    List<RoutePhoto> findByHikingRoute(HikingRoute route);
}

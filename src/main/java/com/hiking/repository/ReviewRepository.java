package com.hiking.repository;

import com.hiking.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUser_Id(Long userId);

    List<Review> findByHikingRoute_IdOrderByIdDesc(Long routeId);
    List<Review> findByHut_IdOrderByIdDesc(Long hutId);
    List<Review> findByLandmark_IdOrderByIdDesc(Long landmarkId);
    List<Review> findByEvent_IdOrderByIdDesc(Long eventId);

    java.util.Optional<Review> findByUser_IdAndHikingRoute_Id(Long userId, Long routeId);
    java.util.Optional<Review> findByUser_IdAndHut_Id(Long userId, Long hutId);
    java.util.Optional<Review> findByUser_IdAndLandmark_Id(Long userId, Long landmarkId);
    java.util.Optional<Review> findByUser_IdAndEvent_Id(Long userId, Long eventId);
}

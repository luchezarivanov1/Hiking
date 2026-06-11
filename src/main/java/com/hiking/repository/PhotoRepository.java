package com.hiking.repository;

import com.hiking.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByHikingRoute_Id(Long routeId);
    List<Photo> findByHut_Id(Long hutId);
    List<Photo> findByLandmark_Id(Long landmarkId);
    List<Photo> findByEvent_Id(Long eventId);
    List<Photo> findByChallenge_Id(Long challengeId);
    List<Photo> findByMountain_Id(Long mountainId);
}

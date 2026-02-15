package com.hiking.repository;

import com.hiking.entity.LandmarkPhoto;
import com.hiking.entity.Landmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandmarkPhotoRepository extends JpaRepository<LandmarkPhoto, Long> {
    List<LandmarkPhoto> findByLandmark(Landmark landmark);
}

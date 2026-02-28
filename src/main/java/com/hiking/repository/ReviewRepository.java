package com.hiking.repository;

import com.hiking.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByHut_Name(String hutName);
    List<Review> findByLandmark_Name(String landmarkName);
    List<Review> findByUser_Id(Long userId);
}

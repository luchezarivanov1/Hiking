package com.hiking.repository;

import com.hiking.entity.Review;
import com.hiking.entity.Mountain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMountain(Mountain mountain);
}

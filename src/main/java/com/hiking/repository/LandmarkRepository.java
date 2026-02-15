package com.hiking.repository;

import com.hiking.entity.Landmark;
import com.hiking.entity.Mountain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandmarkRepository extends JpaRepository<Landmark, Long> {
    List<Landmark> findByMountain(Mountain mountain);
}

package com.hiking.repository;

import com.hiking.entity.Landmark;
import com.hiking.entity.LandmarkType;
import com.hiking.entity.Mountain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandmarkRepository extends JpaRepository<Landmark, Long> {

    List<Landmark> findByMountain(Mountain mountain);

    List<Landmark> findByMountainAndType(Mountain mountain, LandmarkType type);

    List<Landmark> findByMountainAndNameContainingIgnoreCase(Mountain mountain, String name);

    List<Landmark> findByMountainAndTypeAndNameContainingIgnoreCase(Mountain mountain, LandmarkType type, String name);
}

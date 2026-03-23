package com.hiking.repository;

import com.hiking.entity.Mountain;
import com.hiking.entity.MountainPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MountainPhotoRepository extends JpaRepository<MountainPhoto, Long> {
    List<MountainPhoto> findByMountain(Mountain mountain);
}

package com.hiking.repository;

import com.hiking.entity.Hut;
import com.hiking.entity.HutPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HutPhotoRepository extends JpaRepository<HutPhoto, Long> {
    List<HutPhoto> findByHut(Hut hut);
}

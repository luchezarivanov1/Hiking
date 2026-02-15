package com.hiking.repository;

import com.hiking.entity.Hut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HutRepository extends JpaRepository<Hut, Long> {
    List<Hut> findByMountain(String mountain);
}

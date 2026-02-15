package com.hiking.repository;

import com.hiking.entity.Mountain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MountainRepository extends JpaRepository<Mountain, Long> {
    boolean existsByName(String name);
}

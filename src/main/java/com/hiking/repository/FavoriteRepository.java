package com.hiking.repository;

import com.hiking.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUser_IdAndEntityTypeAndEntityId(Long userId, String entityType, Long entityId);

    List<Favorite> findByUser_IdAndEntityType(Long userId, String entityType);

    List<Favorite> findByUser_Id(Long userId);

    boolean existsByUser_IdAndEntityTypeAndEntityId(Long userId, String entityType, Long entityId);

    void deleteByUser_IdAndEntityTypeAndEntityId(Long userId, String entityType, Long entityId);
}

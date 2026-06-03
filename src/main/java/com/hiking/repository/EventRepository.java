package com.hiking.repository;

import com.hiking.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e JOIN e.participants p WHERE p.id = :userId ORDER BY e.startTime DESC NULLS LAST, e.id DESC")
    List<Event> findJoinedByUserId(@Param("userId") Long userId);
}

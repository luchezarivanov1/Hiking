package com.hiking.repository;

import com.hiking.entity.Event;
import com.hiking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT u FROM Event e JOIN e.participants u WHERE e.id = :eventId")
    List<User> findParticipantsByEventId(@Param("eventId") Long eventId);
}

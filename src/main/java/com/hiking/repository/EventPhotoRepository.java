package com.hiking.repository;

import com.hiking.entity.Event;
import com.hiking.entity.EventPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventPhotoRepository extends JpaRepository<EventPhoto, Long> {
    List<EventPhoto> findByEvent(Event event);
}

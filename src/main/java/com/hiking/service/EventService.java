package com.hiking.service;

import com.hiking.entity.Event;
import com.hiking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repo;

    public List<Event> getAll() {
        return repo.findAll();
    }

    public Event create(Event e) {
        return repo.save(e);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

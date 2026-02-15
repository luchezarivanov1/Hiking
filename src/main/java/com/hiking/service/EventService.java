package com.hiking.service;

import com.hiking.dto.EventDTO;
import com.hiking.entity.Event;
import com.hiking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepo;
    private final ModelMapper mapper;

    public List<EventDTO> getAllEvents() {
        return eventRepo.findAll().stream()
                .map(e -> mapper.map(e, EventDTO.class))
                .toList();
    }

    public EventDTO getById(Long id) {
        var event = eventRepo.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        return mapper.map(event, EventDTO.class);
    }

    public EventDTO create(EventDTO dto) {
        var event = mapper.map(dto, Event.class);
        var saved = eventRepo.save(event);
        return mapper.map(saved, EventDTO.class);
    }

    public EventDTO update(Long id, EventDTO dto) {
        var event = eventRepo.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        mapper.map(dto, event);
        event.setId(id); // Ensure ID is preserved
        var updated = eventRepo.save(event);
        return mapper.map(updated, EventDTO.class);
    }

    public void delete(Long id) {
        eventRepo.deleteById(id);
    }
}

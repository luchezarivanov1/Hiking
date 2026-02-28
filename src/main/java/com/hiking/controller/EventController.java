package com.hiking.controller;

import com.hiking.entity.Event;
import com.hiking.entity.User;
import com.hiking.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService service;

    @GetMapping
    public List<Event> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}/users")
    public List<User> getUsersByEvent(@PathVariable Long id) {
        return service.getUsersByEventId(id);
    }

    @PostMapping
    public Event create(@RequestBody Event e) {
        return service.create(e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

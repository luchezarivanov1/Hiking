package com.hiking.controller;

import com.hiking.dto.EventDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.service.EventService;
import com.hiking.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService service;
    private final PhotoService photoService;

    @GetMapping
    public List<EventDTO> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public EventDTO getById(@PathVariable Long id) { return service.getById(id); }

    @GetMapping("/me/joined")
    @PreAuthorize("isAuthenticated()")
    public List<EventDTO> getMyJoined() { return service.getJoinedByCurrentUser(); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EventDTO create(@RequestBody EventDTO dto) { return service.create(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EventDTO update(@PathVariable Long id, @RequestBody EventDTO dto) { return service.update(id, dto); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { service.delete(id); }

    @PostMapping("/{id}/photos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoInfoDTO> addPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        return ResponseEntity.ok(photoService.addPhoto("events", id, file, description));
    }

    @DeleteMapping("/{id}/photos/{photoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id, @PathVariable Long photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    @PreAuthorize("isAuthenticated()")
    public EventDTO join(@PathVariable Long id) { return service.join(id); }

    @DeleteMapping("/{id}/join")
    @PreAuthorize("isAuthenticated()")
    public EventDTO leave(@PathVariable Long id) { return service.leave(id); }
}

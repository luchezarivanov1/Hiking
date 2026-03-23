package com.hiking.service;

import com.hiking.dto.EventDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.Event;
import com.hiking.entity.EventPhoto;
import com.hiking.entity.User;
import com.hiking.repository.EventPhotoRepository;
import com.hiking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repo;
    private final EventPhotoRepository photoRepo;
    private final FileStorageService fileStorageService;
    private final ModelMapper mapper;

    public List<EventDTO> getAll() {
        return repo.findAll().stream().map(this::mapToDTO).toList();
    }

    public EventDTO getById(Long id) {
        return mapToDTO(repo.findById(id).orElseThrow(() -> new RuntimeException("Event not found")));
    }

    public List<User> getUsersByEventId(Long eventId) {
        return repo.findParticipantsByEventId(eventId);
    }

    public EventDTO create(EventDTO dto) {
        var event = mapper.map(dto, Event.class);
        return mapToDTO(repo.save(event));
    }

    public EventDTO update(Long id, EventDTO dto) {
        var event = repo.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setLocation(dto.getLocation());
        return mapToDTO(repo.save(event));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public PhotoInfoDTO addPhoto(Long eventId, MultipartFile file, String description) {
        var event = repo.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        String url = fileStorageService.store(file, "events");
        EventPhoto photo = new EventPhoto();
        photo.setEvent(event);
        photo.setUrl(url);
        photo.setDescription(description);
        EventPhoto saved = photoRepo.save(photo);
        return new PhotoInfoDTO(saved.getId(), saved.getUrl());
    }

    public void deletePhoto(Long photoId) {
        photoRepo.deleteById(photoId);
    }

    private EventDTO mapToDTO(Event event) {
        var dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setLocation(event.getLocation());
        dto.setPhotos(photoRepo.findByEvent(event).stream()
                .map(p -> new PhotoInfoDTO(p.getId(), p.getUrl())).toList());
        return dto;
    }
}

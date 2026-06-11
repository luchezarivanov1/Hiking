package com.hiking.service;

import com.hiking.dto.EventDTO;
import com.hiking.entity.Event;
import com.hiking.entity.User;
import com.hiking.repository.EventRepository;
import com.hiking.repository.UserRepository;
import com.hiking.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repo;
    private final UserRepository userRepo;
    private final PhotoService photoService;
    private final FavoriteService favoriteService;
    private final ModelMapper mapper;

    public List<EventDTO> getAll() {
        return repo.findAll().stream().map(this::mapToDTO).toList();
    }

    public EventDTO getById(Long id) {
        return mapToDTO(repo.findById(id).orElseThrow(() -> new RuntimeException("Event not found")));
    }

    public List<EventDTO> getJoinedByCurrentUser() {
        User user = currentUserOrThrow();
        return repo.findJoinedByUserId(user.getId()).stream().map(this::mapToDTO).toList();
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

    @Transactional
    public EventDTO join(Long eventId) {
        Event event = repo.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        User user = currentUserOrThrow();
        if (event.getParticipants().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            event.getParticipants().add(user);
            repo.save(event);
        }
        return mapToDTO(event);
    }

    @Transactional
    public EventDTO leave(Long eventId) {
        Event event = repo.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        User user = currentUserOrThrow();
        event.getParticipants().removeIf(u -> u.getId().equals(user.getId()));
        repo.save(event);
        return mapToDTO(event);
    }

    private User currentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails details)) {
            throw new RuntimeException("Not authenticated");
        }
        return userRepo.findByEmail(details.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private EventDTO mapToDTO(Event event) {
        var dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setLocation(event.getLocation());
        dto.setPhotos(photoService.getForEntity("events", event.getId()));
        List<User> participants = event.getParticipants();
        dto.setParticipantCount(participants == null ? 0 : participants.size());
        dto.setJoined(isCurrentUserParticipant(participants));
        dto.setFavorited(favoriteService.isFavorite("events", event.getId()));
        return dto;
    }

    private boolean isCurrentUserParticipant(List<User> participants) {
        if (participants == null || participants.isEmpty()) return false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails details)) return false;
        String email = details.getUsername();
        return participants.stream().anyMatch(u -> email.equals(u.getEmail()));
    }
}

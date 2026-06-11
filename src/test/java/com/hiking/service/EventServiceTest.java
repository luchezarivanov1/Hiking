package com.hiking.service;

import com.hiking.dto.EventDTO;
import com.hiking.entity.Event;
import com.hiking.entity.User;
import com.hiking.repository.EventRepository;
import com.hiking.repository.UserRepository;
import com.hiking.support.SecurityContextTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository repo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private PhotoService photoService;
    @Mock
    private FavoriteService favoriteService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private User user;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(20L);
        event.setTitle("Group hike");

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextTestUtils.clear();
    }

    private void stubMapping() {
        when(photoService.getForEntity("events", 20L)).thenReturn(List.of());
        when(favoriteService.isFavorite("events", 20L)).thenReturn(false);
    }

    @Test
    void getAll_mapsEntities() {
        when(repo.findAll()).thenReturn(List.of(event));
        stubMapping();

        List<EventDTO> result = eventService.getAll();

        assertEquals(1, result.size());
        assertEquals("Group hike", result.get(0).getTitle());
    }

    @Test
    void getById_notFound_throws() {
        when(repo.findById(20L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> eventService.getById(20L));
    }

    @Test
    void create_savesMappedEntity() {
        EventDTO input = new EventDTO();
        when(mapper.map(input, Event.class)).thenReturn(event);
        when(repo.save(event)).thenReturn(event);
        stubMapping();

        EventDTO result = eventService.create(input);

        assertEquals(20L, result.getId());
        verify(repo).save(event);
    }

    @Test
    void update_setsFields() {
        EventDTO input = new EventDTO();
        input.setTitle("Renamed");
        input.setLocation("Vitosha");
        when(repo.findById(20L)).thenReturn(Optional.of(event));
        when(repo.save(event)).thenReturn(event);
        stubMapping();

        eventService.update(20L, input);

        assertEquals("Renamed", event.getTitle());
        assertEquals("Vitosha", event.getLocation());
    }

    @Test
    void delete_delegates() {
        eventService.delete(20L);
        verify(repo).deleteById(20L);
    }

    @Test
    void join_whenNotParticipant_addsAndSavesEvent() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(repo.findById(20L)).thenReturn(Optional.of(event));
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        stubMapping();

        eventService.join(20L);

        assertTrue(event.getParticipants().stream().anyMatch(u -> u.getId().equals(1L)));
        verify(repo).save(event);
    }

    @Test
    void join_whenAlreadyParticipant_doesNotSaveAgain() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        event.setParticipants(new ArrayList<>(List.of(user)));
        when(repo.findById(20L)).thenReturn(Optional.of(event));
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        stubMapping();

        eventService.join(20L);

        verify(repo, never()).save(any());
    }

    @Test
    void leave_removesParticipantAndSaves() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        event.setParticipants(new ArrayList<>(List.of(user)));
        when(repo.findById(20L)).thenReturn(Optional.of(event));
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        stubMapping();

        eventService.leave(20L);

        assertTrue(event.getParticipants().isEmpty());
        verify(repo).save(event);
    }

    @Test
    void getJoinedByCurrentUser_queriesByUserId() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repo.findJoinedByUserId(1L)).thenReturn(List.of(event));
        stubMapping();

        List<EventDTO> result = eventService.getJoinedByCurrentUser();

        assertEquals(1, result.size());
        verify(repo).findJoinedByUserId(1L);
    }
}

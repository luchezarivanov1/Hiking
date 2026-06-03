package com.hiking.service;

import com.hiking.dto.ChallengeDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.Challenge;
import com.hiking.entity.ChallengePhoto;
import com.hiking.entity.User;
import com.hiking.repository.ChallengePhotoRepository;
import com.hiking.repository.ChallengeRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository repo;
    @Mock
    private ChallengePhotoRepository photoRepo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private FavoriteService favoriteService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ChallengeService challengeService;

    private Challenge challenge;
    private User user;

    @BeforeEach
    void setUp() {
        challenge = new Challenge();
        challenge.setId(10L);
        challenge.setName("Seven Rila Lakes");
        challenge.setType("PEAKS");
        challenge.setTargetCount(7);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextTestUtils.clear();
    }

    private void stubMapping() {
        when(photoRepo.findByChallenge(challenge)).thenReturn(List.of());
        when(favoriteService.isFavorite("challenges", 10L)).thenReturn(false);
    }

    @Test
    void getAll_mapsEntitiesToDtos() {
        when(repo.findAll()).thenReturn(List.of(challenge));
        stubMapping();

        List<ChallengeDTO> result = challengeService.getAll();

        assertEquals(1, result.size());
        assertEquals("Seven Rila Lakes", result.get(0).getName());
        assertEquals(0, result.get(0).getParticipantCount());
        assertFalse(result.get(0).isFavorited());
    }

    @Test
    void getById_found_returnsDto() {
        when(repo.findById(10L)).thenReturn(Optional.of(challenge));
        stubMapping();

        ChallengeDTO dto = challengeService.getById(10L);

        assertEquals(10L, dto.getId());
    }

    @Test
    void getById_notFound_throws() {
        when(repo.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> challengeService.getById(10L));
    }

    @Test
    void create_savesMappedEntity() {
        ChallengeDTO input = new ChallengeDTO();
        input.setName("New");
        when(mapper.map(input, Challenge.class)).thenReturn(challenge);
        when(repo.save(challenge)).thenReturn(challenge);
        stubMapping();

        ChallengeDTO result = challengeService.create(input);

        assertEquals(10L, result.getId());
        verify(repo).save(challenge);
    }

    @Test
    void update_found_updatesFields() {
        ChallengeDTO input = new ChallengeDTO();
        input.setName("Updated");
        input.setDescription("desc");
        input.setType("DISTANCE");
        input.setTargetCount(3);
        when(repo.findById(10L)).thenReturn(Optional.of(challenge));
        when(repo.save(challenge)).thenReturn(challenge);
        stubMapping();

        challengeService.update(10L, input);

        assertEquals("Updated", challenge.getName());
        assertEquals("DISTANCE", challenge.getType());
        assertEquals(3, challenge.getTargetCount());
    }

    @Test
    void update_notFound_throws() {
        when(repo.findById(10L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> challengeService.update(10L, new ChallengeDTO()));
    }

    @Test
    void delete_delegatesToRepository() {
        challengeService.delete(10L);
        verify(repo).deleteById(10L);
    }

    @Test
    void addPhoto_storesFileAndPersistsPhoto() {
        MultipartFile file = new MockMultipartFile("f", "p.jpg", "image/jpeg", "x".getBytes());
        when(repo.findById(10L)).thenReturn(Optional.of(challenge));
        when(fileStorageService.store(file, "challenges")).thenReturn("http://host/uploads/challenges/x.jpg");
        ChallengePhoto saved = new ChallengePhoto();
        saved.setId(99L);
        saved.setUrl("http://host/uploads/challenges/x.jpg");
        when(photoRepo.save(any(ChallengePhoto.class))).thenReturn(saved);

        PhotoInfoDTO dto = challengeService.addPhoto(10L, file, "summit");

        assertEquals(99L, dto.getId());
        assertEquals("http://host/uploads/challenges/x.jpg", dto.getUrl());
    }

    @Test
    void deletePhoto_delegatesToRepository() {
        challengeService.deletePhoto(5L);
        verify(photoRepo).deleteById(5L);
    }

    @Test
    void join_whenNotJoined_addsAndSavesUser() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(repo.findById(10L)).thenReturn(Optional.of(challenge));
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        stubMapping();

        challengeService.join(10L);

        assertTrue(user.getChallenges().stream().anyMatch(c -> c.getId().equals(10L)));
        verify(userRepo).save(user);
    }

    @Test
    void join_whenAlreadyJoined_doesNotSaveAgain() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        user.setChallenges(new ArrayList<>(List.of(challenge)));
        when(repo.findById(10L)).thenReturn(Optional.of(challenge));
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        stubMapping();

        challengeService.join(10L);

        verify(userRepo, never()).save(any());
    }

    @Test
    void join_notAuthenticated_throws() {
        when(repo.findById(10L)).thenReturn(Optional.of(challenge));
        assertThrows(RuntimeException.class, () -> challengeService.join(10L));
    }

    @Test
    void leave_removesChallengeAndSaves() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        user.setChallenges(new ArrayList<>(List.of(challenge)));
        when(repo.findById(10L)).thenReturn(Optional.of(challenge));
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        stubMapping();

        challengeService.leave(10L);

        assertTrue(user.getChallenges().isEmpty());
        verify(userRepo).save(user);
    }

    @Test
    void getJoinedByCurrentUser_returnsUsersChallenges() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repo.findByUserId(1L)).thenReturn(List.of(challenge));
        stubMapping();

        List<ChallengeDTO> result = challengeService.getJoinedByCurrentUser();

        assertEquals(1, result.size());
        verify(repo).findByUserId(1L);
    }

    @Test
    void mapToDTO_reflectsParticipantCountAndJoinedFlag() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        challenge.setParticipants(new ArrayList<>(List.of(user)));
        when(repo.findById(10L)).thenReturn(Optional.of(challenge));
        when(photoRepo.findByChallenge(challenge)).thenReturn(List.of());
        when(favoriteService.isFavorite(eq("challenges"), eq(10L))).thenReturn(true);

        ChallengeDTO dto = challengeService.getById(10L);

        assertEquals(1, dto.getParticipantCount());
        assertTrue(dto.isJoined());
        assertTrue(dto.isFavorited());
    }
}

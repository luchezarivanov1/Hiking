package com.hiking.service;

import com.hiking.dto.ChallengeDTO;
import com.hiking.entity.Challenge;
import com.hiking.entity.User;
import com.hiking.repository.ChallengeRepository;
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
public class ChallengeService {

    private final ChallengeRepository repo;
    private final UserRepository userRepo;
    private final PhotoService photoService;
    private final FavoriteService favoriteService;
    private final ModelMapper mapper;

    public List<ChallengeDTO> getAll() {
        return repo.findAll().stream().map(this::mapToDTO).toList();
    }

    public ChallengeDTO getById(Long id) {
        return mapToDTO(repo.findById(id).orElseThrow(() -> new RuntimeException("Challenge not found")));
    }

    public List<ChallengeDTO> getJoinedByCurrentUser() {
        User user = currentUserOrThrow();
        return repo.findByUserId(user.getId()).stream().map(this::mapToDTO).toList();
    }

    public ChallengeDTO create(ChallengeDTO dto) {
        var challenge = mapper.map(dto, Challenge.class);
        return mapToDTO(repo.save(challenge));
    }

    public ChallengeDTO update(Long id, ChallengeDTO dto) {
        var challenge = repo.findById(id).orElseThrow(() -> new RuntimeException("Challenge not found"));
        challenge.setName(dto.getName());
        challenge.setDescription(dto.getDescription());
        challenge.setType(dto.getType());
        challenge.setTargetCount(dto.getTargetCount());
        return mapToDTO(repo.save(challenge));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public ChallengeDTO join(Long challengeId) {
        Challenge challenge = repo.findById(challengeId).orElseThrow(() -> new RuntimeException("Challenge not found"));
        User user = currentUserOrThrow();
        if (user.getChallenges().stream().noneMatch(c -> c.getId().equals(challenge.getId()))) {
            user.getChallenges().add(challenge);
            userRepo.save(user);
        }
        return mapToDTO(challenge);
    }

    @Transactional
    public ChallengeDTO leave(Long challengeId) {
        Challenge challenge = repo.findById(challengeId).orElseThrow(() -> new RuntimeException("Challenge not found"));
        User user = currentUserOrThrow();
        user.getChallenges().removeIf(c -> c.getId().equals(challenge.getId()));
        userRepo.save(user);
        return mapToDTO(challenge);
    }

    private User currentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails details)) {
            throw new RuntimeException("Not authenticated");
        }
        return userRepo.findByEmail(details.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ChallengeDTO mapToDTO(Challenge challenge) {
        var dto = new ChallengeDTO();
        dto.setId(challenge.getId());
        dto.setName(challenge.getName());
        dto.setDescription(challenge.getDescription());
        dto.setType(challenge.getType());
        dto.setTargetCount(challenge.getTargetCount());
        dto.setPhotos(photoService.getForEntity("challenges", challenge.getId()));
        List<User> participants = challenge.getParticipants();
        dto.setParticipantCount(participants == null ? 0 : participants.size());
        dto.setJoined(isCurrentUserParticipant(participants));
        dto.setFavorited(favoriteService.isFavorite("challenges", challenge.getId()));
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

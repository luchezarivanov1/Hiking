package com.hiking.service;

import com.hiking.entity.Favorite;
import com.hiking.entity.User;
import com.hiking.repository.FavoriteRepository;
import com.hiking.repository.UserRepository;
import com.hiking.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private static final Set<String> ALLOWED = Set.of(
            "routes", "huts", "landmarks", "mountains", "events", "challenges");

    private final FavoriteRepository repo;
    private final UserRepository userRepo;

    public boolean isFavorite(String type, Long entityId) {
        Long userId = currentUserIdOrNull();
        if (userId == null) return false;
        return repo.existsByUser_IdAndEntityTypeAndEntityId(userId, type, entityId);
    }

    public List<Favorite> listForCurrentUser() {
        Long userId = currentUserIdOrNull();
        if (userId == null) throw new RuntimeException("Not authenticated");
        return repo.findByUser_Id(userId);
    }

    @Transactional
    public void add(String type, Long entityId) {
        validateType(type);
        User user = currentUserOrThrow();
        if (!repo.existsByUser_IdAndEntityTypeAndEntityId(user.getId(), type, entityId)) {
            Favorite fav = new Favorite();
            fav.setUser(user);
            fav.setEntityType(type);
            fav.setEntityId(entityId);
            repo.save(fav);
        }
    }

    @Transactional
    public void remove(String type, Long entityId) {
        validateType(type);
        User user = currentUserOrThrow();
        repo.deleteByUser_IdAndEntityTypeAndEntityId(user.getId(), type, entityId);
    }

    private void validateType(String type) {
        if (!ALLOWED.contains(type)) {
            throw new RuntimeException("Unsupported favorite type: " + type);
        }
    }

    private Long currentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails details)) return null;
        return Optional.ofNullable(details.getUser()).map(User::getId).orElse(null);
    }

    private User currentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails details)) {
            throw new RuntimeException("Not authenticated");
        }
        return userRepo.findByEmail(details.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

package com.hiking.service;

import com.hiking.entity.Favorite;
import com.hiking.entity.User;
import com.hiking.repository.FavoriteRepository;
import com.hiking.repository.UserRepository;
import com.hiking.support.SecurityContextTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository repo;
    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private FavoriteService favoriteService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextTestUtils.clear();
    }

    @Test
    void isFavorite_notAuthenticated_returnsFalse() {
        assertFalse(favoriteService.isFavorite("routes", 5L));
        verifyNoInteractions(repo);
    }

    @Test
    void isFavorite_authenticated_delegatesToRepository() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(repo.existsByUser_IdAndEntityTypeAndEntityId(1L, "routes", 5L)).thenReturn(true);

        assertTrue(favoriteService.isFavorite("routes", 5L));
    }

    @Test
    void listForCurrentUser_notAuthenticated_throws() {
        assertThrows(RuntimeException.class, () -> favoriteService.listForCurrentUser());
    }

    @Test
    void listForCurrentUser_authenticated_returnsFavorites() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        Favorite fav = new Favorite();
        when(repo.findByUser_Id(1L)).thenReturn(List.of(fav));

        List<Favorite> result = favoriteService.listForCurrentUser();

        assertEquals(1, result.size());
    }

    @Test
    void add_unsupportedType_throws() {
        assertThrows(RuntimeException.class, () -> favoriteService.add("unicorns", 5L));
    }

    @Test
    void add_newFavorite_persists() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repo.existsByUser_IdAndEntityTypeAndEntityId(1L, "huts", 5L)).thenReturn(false);

        favoriteService.add("huts", 5L);

        verify(repo).save(any(Favorite.class));
    }

    @Test
    void add_alreadyFavorited_doesNotPersistAgain() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repo.existsByUser_IdAndEntityTypeAndEntityId(1L, "huts", 5L)).thenReturn(true);

        favoriteService.add("huts", 5L);

        verify(repo, never()).save(any());
    }

    @Test
    void remove_validType_deletesFavorite() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        favoriteService.remove("mountains", 9L);

        verify(repo).deleteByUser_IdAndEntityTypeAndEntityId(1L, "mountains", 9L);
    }

    @Test
    void remove_unsupportedType_throwsBeforeLookup() {
        assertThrows(RuntimeException.class, () -> favoriteService.remove("nope", 1L));
        verifyNoInteractions(userRepo);
    }
}

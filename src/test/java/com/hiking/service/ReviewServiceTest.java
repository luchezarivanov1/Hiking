package com.hiking.service;

import com.hiking.dto.ReviewDTO;
import com.hiking.entity.HikingRoute;
import com.hiking.entity.Review;
import com.hiking.entity.User;
import com.hiking.exception.BadRequestException;
import com.hiking.exception.ResourceNotFoundException;
import com.hiking.exception.UnauthorizedException;
import com.hiking.repository.EventRepository;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.HutRepository;
import com.hiking.repository.LandmarkRepository;
import com.hiking.repository.ReviewRepository;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository repo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private HikingRouteRepository routeRepo;
    @Mock
    private HutRepository hutRepo;
    @Mock
    private LandmarkRepository landmarkRepo;
    @Mock
    private EventRepository eventRepo;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private HikingRoute route;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setUsername("hiker");

        route = new HikingRoute();
        route.setId(5L);
        route.setName("Malyovitsa");
    }

    @AfterEach
    void tearDown() {
        SecurityContextTestUtils.clear();
    }

    private Review reviewBy(User owner, int rating) {
        Review r = new Review();
        r.setId(100L);
        r.setUser(owner);
        r.setRating(rating);
        r.setHikingRoute(route);
        return r;
    }

    @Test
    void getByUser_mapsReviews() {
        when(repo.findByUser_Id(1L)).thenReturn(List.of(reviewBy(user, 4)));

        List<ReviewDTO> result = reviewService.getByUser(1L);

        assertEquals(1, result.size());
        assertEquals("routes", result.get(0).getEntityType());
        assertEquals("Malyovitsa", result.get(0).getEntityName());
        assertEquals("hiker", result.get(0).getUsername());
    }

    @Test
    void getForEntity_routes_queriesRouteReviews() {
        when(repo.findByHikingRoute_IdOrderByIdDesc(5L)).thenReturn(List.of(reviewBy(user, 5)));

        List<ReviewDTO> result = reviewService.getForEntity("routes", 5L);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRating());
    }

    @Test
    void getForEntity_unsupportedType_throws() {
        assertThrows(BadRequestException.class, () -> reviewService.getForEntity("mountains", 1L));
    }

    @Test
    void getSummary_computesAverageAndCount() {
        when(repo.findByHut_IdOrderByIdDesc(8L)).thenReturn(List.of(
                reviewBy(user, 4), reviewBy(user, 2)));

        Map<String, Object> summary = reviewService.getSummary("huts", 8L);

        assertEquals(3.0, (double) summary.get("average"), 1e-9);
        assertEquals(2, summary.get("count"));
    }

    @Test
    void getSummary_noReviews_averageIsZero() {
        when(repo.findByLandmark_IdOrderByIdDesc(8L)).thenReturn(List.of());

        Map<String, Object> summary = reviewService.getSummary("landmarks", 8L);

        assertEquals(0.0, (double) summary.get("average"), 1e-9);
        assertEquals(0, summary.get("count"));
    }

    @Test
    void create_validReview_persists() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repo.findByUser_IdAndHikingRoute_Id(1L, 5L)).thenReturn(Optional.empty());
        when(routeRepo.findById(5L)).thenReturn(Optional.of(route));
        when(repo.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        ReviewDTO dto = reviewService.create("routes", 5L, 4, "Great hike");

        assertEquals(4, dto.getRating());
        assertEquals("routes", dto.getEntityType());
        verify(repo).save(any(Review.class));
    }

    @Test
    void create_invalidRating_throwsBeforeLookup() {
        assertThrows(BadRequestException.class, () -> reviewService.create("routes", 5L, 6, "x"));
        verifyNoInteractions(repo, userRepo, routeRepo);
    }

    @Test
    void create_nullRating_throws() {
        assertThrows(BadRequestException.class, () -> reviewService.create("routes", 5L, null, "x"));
    }

    @Test
    void create_duplicateReview_throws() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repo.findByUser_IdAndHikingRoute_Id(1L, 5L)).thenReturn(Optional.of(reviewBy(user, 3)));

        assertThrows(BadRequestException.class, () -> reviewService.create("routes", 5L, 4, "dup"));
        verify(repo, never()).save(any());
    }

    @Test
    void create_unknownEntity_throwsNotFound() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repo.findByUser_IdAndHikingRoute_Id(1L, 5L)).thenReturn(Optional.empty());
        when(routeRepo.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.create("routes", 5L, 4, "x"));
    }

    @Test
    void update_ownReview_succeeds() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        Review existing = reviewBy(user, 3);
        when(repo.findById(100L)).thenReturn(Optional.of(existing));
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repo.save(existing)).thenReturn(existing);

        ReviewDTO dto = reviewService.update(100L, 5, "updated");

        assertEquals(5, dto.getRating());
        assertEquals(5, existing.getRating());
    }

    @Test
    void update_othersReviewAsNonAdmin_throwsUnauthorized() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        User owner = new User();
        owner.setId(2L);
        Review existing = reviewBy(owner, 3);
        when(repo.findById(100L)).thenReturn(Optional.of(existing));
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> reviewService.update(100L, 5, "x"));
    }

    @Test
    void update_othersReviewAsAdmin_succeeds() {
        SecurityContextTestUtils.authenticate(user, "ROLE_ADMIN");
        User owner = new User();
        owner.setId(2L);
        Review existing = reviewBy(owner, 3);
        when(repo.findById(100L)).thenReturn(Optional.of(existing));
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repo.save(existing)).thenReturn(existing);

        ReviewDTO dto = reviewService.update(100L, 1, "moderated");

        assertEquals(1, dto.getRating());
    }

    @Test
    void update_notFound_throws() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(repo.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.update(100L, 4, "x"));
    }

    @Test
    void delete_ownReview_succeeds() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        Review existing = reviewBy(user, 3);
        when(repo.findById(100L)).thenReturn(Optional.of(existing));
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        reviewService.delete(100L);

        verify(repo).delete(existing);
    }

    @Test
    void delete_othersReviewAsNonAdmin_throwsUnauthorized() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        User owner = new User();
        owner.setId(2L);
        Review existing = reviewBy(owner, 3);
        when(repo.findById(100L)).thenReturn(Optional.of(existing));
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> reviewService.delete(100L));
        verify(repo, never()).delete(any());
    }
}

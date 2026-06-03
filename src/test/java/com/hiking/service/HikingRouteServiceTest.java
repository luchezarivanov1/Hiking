package com.hiking.service;

import com.hiking.dto.HikingRouteDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.HikingRoute;
import com.hiking.entity.Mountain;
import com.hiking.entity.RoutePhoto;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.MountainRepository;
import com.hiking.repository.RoutePhotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HikingRouteServiceTest {

    @Mock
    private HikingRouteRepository routeRepo;
    @Mock
    private MountainRepository mountainRepo;
    @Mock
    private RoutePhotoRepository photoRepo;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private FavoriteService favoriteService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private HikingRouteService routeService;

    private HikingRoute route;
    private Mountain mountain;

    @BeforeEach
    void setUp() {
        route = new HikingRoute();
        route.setId(5L);
        route.setName("Malyovitsa");

        mountain = new Mountain();
        mountain.setId(3L);
    }

    private void stubMapping() {
        when(photoRepo.findByHikingRoute(route)).thenReturn(List.of());
        when(favoriteService.isFavorite("routes", 5L)).thenReturn(false);
    }

    @Test
    void getAllRoutes_mapsEntities() {
        when(routeRepo.findAll()).thenReturn(List.of(route));
        stubMapping();

        List<HikingRouteDTO> result = routeService.getAllRoutes();

        assertEquals(1, result.size());
        assertEquals("Malyovitsa", result.get(0).getName());
    }

    @Test
    void getById_notFound_throws() {
        when(routeRepo.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> routeService.getById(5L));
    }

    @Test
    void create_withoutMountain_leavesMountainNull() {
        HikingRouteDTO input = new HikingRouteDTO();
        when(mapper.map(input, HikingRoute.class)).thenReturn(route);
        when(routeRepo.save(route)).thenReturn(route);
        stubMapping();

        HikingRouteDTO result = routeService.create(input);

        assertNull(route.getMountain());
        assertNull(result.getMountainId());
    }

    @Test
    void create_withMountain_resolvesAndAttachesIt() {
        HikingRouteDTO input = new HikingRouteDTO();
        input.setMountainId(3L);
        when(mapper.map(input, HikingRoute.class)).thenReturn(route);
        when(mountainRepo.findById(3L)).thenReturn(Optional.of(mountain));
        when(routeRepo.save(route)).thenReturn(route);
        stubMapping();

        HikingRouteDTO result = routeService.create(input);

        assertEquals(mountain, route.getMountain());
        assertEquals(3L, result.getMountainId());
    }

    @Test
    void create_withUnknownMountain_throws() {
        HikingRouteDTO input = new HikingRouteDTO();
        input.setMountainId(99L);
        when(mapper.map(input, HikingRoute.class)).thenReturn(route);
        when(mountainRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> routeService.create(input));
    }

    @Test
    void update_setsFieldsAndResolvesMountain() {
        HikingRouteDTO input = new HikingRouteDTO();
        input.setName("New name");
        input.setDistanceKm(12.5);
        input.setDurationMin(180);
        input.setDifficulty("HARD");
        input.setMountainId(3L);
        when(routeRepo.findById(5L)).thenReturn(Optional.of(route));
        when(mountainRepo.findById(3L)).thenReturn(Optional.of(mountain));
        when(routeRepo.save(route)).thenReturn(route);
        stubMapping();

        routeService.update(5L, input);

        assertEquals("New name", route.getName());
        assertEquals(12.5, route.getDistanceKm());
        assertEquals("HARD", route.getDifficulty());
        assertEquals(mountain, route.getMountain());
    }

    @Test
    void delete_delegates() {
        routeService.delete(5L);
        verify(routeRepo).deleteById(5L);
    }

    @Test
    void addPhoto_storesAndPersists() {
        MultipartFile file = new MockMultipartFile("f", "p.jpg", "image/jpeg", "x".getBytes());
        when(routeRepo.findById(5L)).thenReturn(Optional.of(route));
        when(fileStorageService.store(file, "routes")).thenReturn("url");
        RoutePhoto saved = new RoutePhoto();
        saved.setId(6L);
        saved.setUrl("url");
        when(photoRepo.save(any(RoutePhoto.class))).thenReturn(saved);

        PhotoInfoDTO dto = routeService.addPhoto(5L, file, "view");

        assertEquals(6L, dto.getId());
    }

    @Test
    void deletePhoto_delegates() {
        routeService.deletePhoto(4L);
        verify(photoRepo).deleteById(4L);
    }
}

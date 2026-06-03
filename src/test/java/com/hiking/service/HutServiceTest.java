package com.hiking.service;

import com.hiking.dto.HutDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.HikingRoute;
import com.hiking.entity.Hut;
import com.hiking.entity.HutPhoto;
import com.hiking.entity.Mountain;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.HutPhotoRepository;
import com.hiking.repository.HutRepository;
import com.hiking.repository.MountainRepository;
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
class HutServiceTest {

    @Mock
    private HutRepository hutRepo;
    @Mock
    private MountainRepository mountainRepo;
    @Mock
    private HikingRouteRepository routeRepo;
    @Mock
    private HutPhotoRepository photoRepo;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private FavoriteService favoriteService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private HutService hutService;

    private Hut hut;

    @BeforeEach
    void setUp() {
        hut = new Hut();
        hut.setId(4L);
        hut.setName("Malyovitsa Hut");
    }

    private void stubMapping() {
        when(photoRepo.findByHut(hut)).thenReturn(List.of());
        when(favoriteService.isFavorite("huts", 4L)).thenReturn(false);
    }

    @Test
    void getAllHuts_mapsEntities() {
        when(hutRepo.findAll()).thenReturn(List.of(hut));
        stubMapping();

        List<HutDTO> result = hutService.getAllHuts();

        assertEquals(1, result.size());
        assertEquals("Malyovitsa Hut", result.get(0).getName());
    }

    @Test
    void getById_notFound_throws() {
        when(hutRepo.findById(4L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> hutService.getById(4L));
    }

    @Test
    void create_resolvesMountainAndRoute() {
        Mountain mountain = new Mountain();
        mountain.setId(3L);
        HikingRoute route = new HikingRoute();
        route.setId(7L);
        HutDTO input = new HutDTO();
        input.setMountainId(3L);
        input.setRouteId(7L);
        when(mapper.map(input, Hut.class)).thenReturn(hut);
        when(mountainRepo.findById(3L)).thenReturn(Optional.of(mountain));
        when(routeRepo.findById(7L)).thenReturn(Optional.of(route));
        when(hutRepo.save(hut)).thenReturn(hut);
        stubMapping();

        HutDTO result = hutService.create(input);

        assertEquals(mountain, hut.getMountain());
        assertEquals(route, hut.getHikingRoute());
        assertEquals(3L, result.getMountainId());
        assertEquals(7L, result.getRouteId());
    }

    @Test
    void create_withoutAssociations_savesPlainHut() {
        HutDTO input = new HutDTO();
        when(mapper.map(input, Hut.class)).thenReturn(hut);
        when(hutRepo.save(hut)).thenReturn(hut);
        stubMapping();

        hutService.create(input);

        verify(mountainRepo, never()).findById(any());
        verify(routeRepo, never()).findById(any());
    }

    @Test
    void update_setsFieldsAndClearsAssociationsWhenNull() {
        HutDTO input = new HutDTO();
        input.setName("Renamed");
        input.setCapacity(40);
        when(hutRepo.findById(4L)).thenReturn(Optional.of(hut));
        when(hutRepo.save(hut)).thenReturn(hut);
        stubMapping();

        hutService.update(4L, input);

        assertEquals("Renamed", hut.getName());
        assertEquals(40, hut.getCapacity());
        assertNull(hut.getMountain());
        assertNull(hut.getHikingRoute());
    }

    @Test
    void update_notFound_throws() {
        when(hutRepo.findById(4L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> hutService.update(4L, new HutDTO()));
    }

    @Test
    void delete_delegates() {
        hutService.delete(4L);
        verify(hutRepo).deleteById(4L);
    }

    @Test
    void addPhoto_storesAndPersists() {
        MultipartFile file = new MockMultipartFile("f", "p.jpg", "image/jpeg", "x".getBytes());
        when(hutRepo.findById(4L)).thenReturn(Optional.of(hut));
        when(fileStorageService.store(file, "huts")).thenReturn("url");
        HutPhoto saved = new HutPhoto();
        saved.setId(11L);
        saved.setUrl("url");
        when(photoRepo.save(any(HutPhoto.class))).thenReturn(saved);

        PhotoInfoDTO dto = hutService.addPhoto(4L, file, "front");

        assertEquals(11L, dto.getId());
    }

    @Test
    void deletePhoto_delegates() {
        hutService.deletePhoto(1L);
        verify(photoRepo).deleteById(1L);
    }
}

package com.hiking.service;

import com.hiking.dto.LandmarkDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.Landmark;
import com.hiking.entity.LandmarkPhoto;
import com.hiking.entity.LandmarkType;
import com.hiking.entity.Mountain;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.LandmarkPhotoRepository;
import com.hiking.repository.LandmarkRepository;
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
class LandmarkServiceTest {

    @Mock
    private LandmarkRepository landmarkRepo;
    @Mock
    private MountainRepository mountainRepo;
    @Mock
    private HikingRouteRepository routeRepo;
    @Mock
    private LandmarkPhotoRepository photoRepo;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private FavoriteService favoriteService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private LandmarkService landmarkService;

    private Landmark landmark;

    @BeforeEach
    void setUp() {
        landmark = new Landmark();
        landmark.setId(2L);
        landmark.setName("Raysko Praskalo");
        landmark.setType(LandmarkType.WATERFALL);
    }

    private void stubMapping() {
        when(photoRepo.findByLandmark(landmark)).thenReturn(List.of());
        when(favoriteService.isFavorite("landmarks", 2L)).thenReturn(false);
    }

    @Test
    void getAllLandmarks_mapsEntitiesIncludingTypeName() {
        when(landmarkRepo.findAll()).thenReturn(List.of(landmark));
        stubMapping();

        List<LandmarkDTO> result = landmarkService.getAllLandmarks();

        assertEquals(1, result.size());
        assertEquals("WATERFALL", result.get(0).getType());
    }

    @Test
    void getById_notFound_throws() {
        when(landmarkRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> landmarkService.getById(2L));
    }

    @Test
    void create_resolvesMountainAndRoute() {
        Mountain mountain = new Mountain();
        mountain.setId(3L);
        LandmarkDTO input = new LandmarkDTO();
        input.setMountainId(3L);
        when(mapper.map(input, Landmark.class)).thenReturn(landmark);
        when(mountainRepo.findById(3L)).thenReturn(Optional.of(mountain));
        when(landmarkRepo.save(landmark)).thenReturn(landmark);
        stubMapping();

        LandmarkDTO result = landmarkService.create(input);

        assertEquals(mountain, landmark.getMountain());
        assertEquals(3L, result.getMountainId());
    }

    @Test
    void update_validTypeString_parsesEnum() {
        LandmarkDTO input = new LandmarkDTO();
        input.setName("Updated");
        input.setType("PEAK");
        input.setLatitude(42.0);
        input.setLongitude(23.0);
        when(landmarkRepo.findById(2L)).thenReturn(Optional.of(landmark));
        when(landmarkRepo.save(landmark)).thenReturn(landmark);
        stubMapping();

        landmarkService.update(2L, input);

        assertEquals("Updated", landmark.getName());
        assertEquals(LandmarkType.PEAK, landmark.getType());
    }

    @Test
    void update_invalidTypeString_keepsExistingType() {
        LandmarkDTO input = new LandmarkDTO();
        input.setType("NOT_A_TYPE");
        when(landmarkRepo.findById(2L)).thenReturn(Optional.of(landmark));
        when(landmarkRepo.save(landmark)).thenReturn(landmark);
        stubMapping();

        landmarkService.update(2L, input);

        // Invalid value is swallowed; original type is retained
        assertEquals(LandmarkType.WATERFALL, landmark.getType());
    }

    @Test
    void delete_delegates() {
        landmarkService.delete(2L);
        verify(landmarkRepo).deleteById(2L);
    }

    @Test
    void addPhoto_storesAndPersists() {
        MultipartFile file = new MockMultipartFile("f", "p.jpg", "image/jpeg", "x".getBytes());
        when(landmarkRepo.findById(2L)).thenReturn(Optional.of(landmark));
        when(fileStorageService.store(file, "landmarks")).thenReturn("url");
        LandmarkPhoto saved = new LandmarkPhoto();
        saved.setId(12L);
        saved.setUrl("url");
        when(photoRepo.save(any(LandmarkPhoto.class))).thenReturn(saved);

        PhotoInfoDTO dto = landmarkService.addPhoto(2L, file, "falls");

        assertEquals(12L, dto.getId());
    }

    @Test
    void deletePhoto_delegates() {
        landmarkService.deletePhoto(1L);
        verify(photoRepo).deleteById(1L);
    }
}

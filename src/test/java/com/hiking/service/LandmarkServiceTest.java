package com.hiking.service;

import com.hiking.dto.LandmarkDTO;
import com.hiking.entity.Landmark;
import com.hiking.entity.LandmarkType;
import com.hiking.entity.Mountain;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.LandmarkRepository;
import com.hiking.repository.MountainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private PhotoService photoService;
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
        when(photoService.getForEntity("landmarks", 2L)).thenReturn(List.of());
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
}

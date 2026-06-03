package com.hiking.service;

import com.hiking.dto.MountainDTO;
import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.Mountain;
import com.hiking.entity.MountainPhoto;
import com.hiking.repository.MountainPhotoRepository;
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
class MountainServiceTest {

    @Mock
    private MountainRepository mountainRepo;
    @Mock
    private MountainPhotoRepository photoRepo;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private FavoriteService favoriteService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private MountainService mountainService;

    private Mountain mountain;

    @BeforeEach
    void setUp() {
        mountain = new Mountain();
        mountain.setId(3L);
        mountain.setName("Rila");
        mountain.setRegion("Southwest");
        mountain.setHighestPeak(2925.0); // Musala, m
    }

    private void stubMapping() {
        when(photoRepo.findByMountain(mountain)).thenReturn(List.of());
        when(favoriteService.isFavorite("mountains", 3L)).thenReturn(false);
    }

    @Test
    void getAllMountains_mapsEntities() {
        when(mountainRepo.findAll()).thenReturn(List.of(mountain));
        stubMapping();

        List<MountainDTO> result = mountainService.getAllMountains();

        assertEquals(1, result.size());
        assertEquals("Rila", result.get(0).getName());
        assertEquals(2925.0, result.get(0).getHighestPeak());
    }

    @Test
    void getById_found() {
        when(mountainRepo.findById(3L)).thenReturn(Optional.of(mountain));
        stubMapping();

        assertEquals("Rila", mountainService.getById(3L).getName());
    }

    @Test
    void getById_notFound_throws() {
        when(mountainRepo.findById(3L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> mountainService.getById(3L));
    }

    @Test
    void create_savesMappedEntity() {
        MountainDTO input = new MountainDTO();
        when(mapper.map(input, Mountain.class)).thenReturn(mountain);
        when(mountainRepo.save(mountain)).thenReturn(mountain);
        stubMapping();

        MountainDTO result = mountainService.create(input);

        assertEquals(3L, result.getId());
        verify(mountainRepo).save(mountain);
    }

    @Test
    void update_setsFields() {
        MountainDTO input = new MountainDTO();
        input.setName("Pirin");
        input.setRegion("South");
        input.setHighestPeak(2914.0); // Vihren, m
        when(mountainRepo.findById(3L)).thenReturn(Optional.of(mountain));
        when(mountainRepo.save(mountain)).thenReturn(mountain);
        stubMapping();

        mountainService.update(3L, input);

        assertEquals("Pirin", mountain.getName());
        assertEquals(2914.0, mountain.getHighestPeak());
    }

    @Test
    void update_notFound_throws() {
        when(mountainRepo.findById(3L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> mountainService.update(3L, new MountainDTO()));
    }

    @Test
    void delete_delegates() {
        mountainService.delete(3L);
        verify(mountainRepo).deleteById(3L);
    }

    @Test
    void addPhoto_storesAndPersists() {
        MultipartFile file = new MockMultipartFile("f", "p.jpg", "image/jpeg", "x".getBytes());
        when(mountainRepo.findById(3L)).thenReturn(Optional.of(mountain));
        when(fileStorageService.store(file, "mountains")).thenReturn("url");
        MountainPhoto saved = new MountainPhoto();
        saved.setId(8L);
        saved.setUrl("url");
        when(photoRepo.save(any(MountainPhoto.class))).thenReturn(saved);

        PhotoInfoDTO dto = mountainService.addPhoto(3L, file, "view");

        assertEquals(8L, dto.getId());
        assertEquals("url", dto.getUrl());
    }

    @Test
    void deletePhoto_delegates() {
        mountainService.deletePhoto(2L);
        verify(photoRepo).deleteById(2L);
    }
}

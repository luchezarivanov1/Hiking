package com.hiking.service;

import com.hiking.dto.PhotoInfoDTO;
import com.hiking.entity.Hut;
import com.hiking.entity.Photo;
import com.hiking.exception.BadRequestException;
import com.hiking.exception.ResourceNotFoundException;
import com.hiking.repository.ChallengeRepository;
import com.hiking.repository.EventRepository;
import com.hiking.repository.HikingRouteRepository;
import com.hiking.repository.HutRepository;
import com.hiking.repository.LandmarkRepository;
import com.hiking.repository.MountainRepository;
import com.hiking.repository.PhotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepo;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private HikingRouteRepository routeRepo;
    @Mock
    private HutRepository hutRepo;
    @Mock
    private LandmarkRepository landmarkRepo;
    @Mock
    private EventRepository eventRepo;
    @Mock
    private ChallengeRepository challengeRepo;
    @Mock
    private MountainRepository mountainRepo;

    @InjectMocks
    private PhotoService photoService;

    @Test
    void addPhoto_attachesEntityStoresFileAndPersists() {
        Hut hut = new Hut();
        hut.setId(4L);
        MultipartFile file = new MockMultipartFile("f", "p.jpg", "image/jpeg", "x".getBytes());
        when(hutRepo.findById(4L)).thenReturn(Optional.of(hut));
        when(fileStorageService.store(file, "huts")).thenReturn("http://host/uploads/huts/x.jpg");
        Photo saved = new Photo();
        saved.setId(11L);
        saved.setUrl("http://host/uploads/huts/x.jpg");
        when(photoRepo.save(any(Photo.class))).thenReturn(saved);

        PhotoInfoDTO dto = photoService.addPhoto("huts", 4L, file, "front");

        assertThat(dto.getId()).isEqualTo(11L);
        assertThat(dto.getUrl()).isEqualTo("http://host/uploads/huts/x.jpg");
    }

    @Test
    void addPhoto_unknownType_throwsAndStoresNothing() {
        MultipartFile file = new MockMultipartFile("f", "p.jpg", "image/jpeg", "x".getBytes());

        assertThatThrownBy(() -> photoService.addPhoto("dragons", 1L, file, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("dragons");

        verify(fileStorageService, never()).store(any(), any());
        verify(photoRepo, never()).save(any());
    }

    @Test
    void addPhoto_entityNotFound_throwsAndStoresNothing() {
        MultipartFile file = new MockMultipartFile("f", "p.jpg", "image/jpeg", "x".getBytes());
        when(hutRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> photoService.addPhoto("huts", 99L, file, null))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(fileStorageService, never()).store(any(), any());
        verify(photoRepo, never()).save(any());
    }

    @Test
    void deletePhoto_delegatesToRepository() {
        photoService.deletePhoto(7L);
        verify(photoRepo).deleteById(7L);
    }

    @Test
    void getForEntity_mapsPhotosToDtos() {
        Photo p = new Photo();
        p.setId(3L);
        p.setUrl("http://host/uploads/routes/y.jpg");
        when(photoRepo.findByHikingRoute_Id(5L)).thenReturn(List.of(p));

        List<PhotoInfoDTO> result = photoService.getForEntity("routes", 5L);

        assertThat(result).singleElement()
                .satisfies(dto -> {
                    assertThat(dto.getId()).isEqualTo(3L);
                    assertThat(dto.getUrl()).isEqualTo("http://host/uploads/routes/y.jpg");
                });
    }

    @Test
    void getForEntity_unknownType_throws() {
        assertThatThrownBy(() -> photoService.getForEntity("dragons", 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("dragons");
    }
}

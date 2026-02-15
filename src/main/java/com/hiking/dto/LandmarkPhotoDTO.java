package com.hiking.dto;

import lombok.Data;

@Data
public class LandmarkPhotoDTO {
    private Long id;
    private String url;
    private String description;
    private Long landmarkId;
}

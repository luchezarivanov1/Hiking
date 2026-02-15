package com.hiking.dto;

import lombok.Data;

@Data
public class RoutePhotoDTO {
    private Long id;
    private String url;
    private String description;
    private Long routeId;
}

package com.hiking.dto;

import lombok.Data;

@Data
public class HikingRouteDTO {
    private Long id;
    private String name;
    private String mountain;
    private Double distanceKm;
    private Integer durationMin;
    private String difficulty;
    private String description;
}

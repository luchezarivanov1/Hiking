package com.hiking.dto;

import lombok.Data;

@Data
public class HutDTO {
    private Long id;
    private String name;
    private String address;
    private Integer capacity;
    private Boolean openYearRound;
    private Long mountainId;
    private Long routeId;
    private Double rating; // average rating
}

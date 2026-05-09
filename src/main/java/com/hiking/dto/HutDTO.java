package com.hiking.dto;

import lombok.Data;

import java.util.List;

@Data
public class HutDTO {
    private Long id;
    private String name;
    private String address;
    private Integer capacity;
    private Boolean openYearRound;
    private Integer elevationM;
    private Double latitude;
    private Double longitude;
    private Boolean hasRestaurant;
    private Boolean hasAccommodation;
    private String phone;
    private Long mountainId;
    private Long routeId;
    private List<PhotoInfoDTO> photos;
    private boolean favorited;
}

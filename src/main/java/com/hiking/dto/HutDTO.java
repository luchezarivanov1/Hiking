package com.hiking.dto;

import lombok.Data;

@Data
public class HutDTO {
    private Long id;
    private String name;
    private String mountain;
    private String location;
    private Integer beds;
    private Boolean hasRestaurant;
    private Long routeId;
}

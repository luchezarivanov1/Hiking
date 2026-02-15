package com.hiking.dto;

import lombok.Data;

@Data
public class MountainDTO {
    private Long id;
    private String name;
    private String country;
    private Integer maxHeight;
}

package com.hiking.dto;

import lombok.Data;

import java.util.List;

@Data
public class MountainDTO {
    private Long id;
    private String name;
    private String region;
    private Double highestPeak;
    private List<PhotoInfoDTO> photos;
}

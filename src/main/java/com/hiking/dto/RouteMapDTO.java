package com.hiking.dto;

import lombok.Data;
import java.util.List;

@Data
public class RouteMapDTO {
    private Long routeId;
    private List<List<Double>> coordinates;
}

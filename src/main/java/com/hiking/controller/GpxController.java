package com.hiking.controller;

import com.hiking.entity.HikingRoute;
import com.hiking.service.GpxService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/gpx")
@RequiredArgsConstructor
public class GpxController {

    private final GpxService gpxService;

    @PostMapping("/upload")
    public HikingRoute upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam String routeName
    ) {
        return gpxService.importGpx(file, routeName);
    }
}

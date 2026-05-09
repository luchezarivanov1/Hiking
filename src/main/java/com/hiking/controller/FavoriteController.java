package com.hiking.controller;

import com.hiking.dto.ChallengeDTO;
import com.hiking.dto.EventDTO;
import com.hiking.dto.HikingRouteDTO;
import com.hiking.dto.HutDTO;
import com.hiking.dto.LandmarkDTO;
import com.hiking.dto.MountainDTO;
import com.hiking.entity.Favorite;
import com.hiking.service.ChallengeService;
import com.hiking.service.EventService;
import com.hiking.service.FavoriteService;
import com.hiking.service.HikingRouteService;
import com.hiking.service.HutService;
import com.hiking.service.LandmarkService;
import com.hiking.service.MountainService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final HikingRouteService routeService;
    private final HutService hutService;
    private final LandmarkService landmarkService;
    private final MountainService mountainService;
    private final EventService eventService;
    private final ChallengeService challengeService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Map<String, List<?>> listMine() {
        Map<String, List<?>> grouped = new LinkedHashMap<>();
        grouped.put("routes", new java.util.ArrayList<HikingRouteDTO>());
        grouped.put("huts", new java.util.ArrayList<HutDTO>());
        grouped.put("landmarks", new java.util.ArrayList<LandmarkDTO>());
        grouped.put("mountains", new java.util.ArrayList<MountainDTO>());
        grouped.put("events", new java.util.ArrayList<EventDTO>());
        grouped.put("challenges", new java.util.ArrayList<ChallengeDTO>());

        for (Favorite f : favoriteService.listForCurrentUser()) {
            try {
                Object dto = switch (f.getEntityType()) {
                    case "routes" -> routeService.getById(f.getEntityId());
                    case "huts" -> hutService.getById(f.getEntityId());
                    case "landmarks" -> landmarkService.getById(f.getEntityId());
                    case "mountains" -> mountainService.getById(f.getEntityId());
                    case "events" -> eventService.getById(f.getEntityId());
                    case "challenges" -> challengeService.getById(f.getEntityId());
                    default -> null;
                };
                if (dto != null) {
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    List list = grouped.get(f.getEntityType());
                    list.add(dto);
                }
            } catch (RuntimeException ignored) {
                // entity may have been deleted; skip
            }
        }
        return grouped;
    }

    @PostMapping("/{type}/{id}")
    @PreAuthorize("isAuthenticated()")
    public void add(@PathVariable String type, @PathVariable Long id) {
        favoriteService.add(type, id);
    }

    @DeleteMapping("/{type}/{id}")
    @PreAuthorize("isAuthenticated()")
    public void remove(@PathVariable String type, @PathVariable Long id) {
        favoriteService.remove(type, id);
    }
}

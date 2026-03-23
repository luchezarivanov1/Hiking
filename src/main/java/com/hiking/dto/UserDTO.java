package com.hiking.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String experienceLevel;
    private String profileImageUrl;
    private String firstName;
    private String lastName;
    private Integer age;
    private String city;
    private Double totalDistanceKm;
    private Integer totalHikesCompleted;
    private List<String> roles;
    private List<Long> friendIds;
    private boolean accountLocked;
}

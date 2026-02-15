package com.hiking.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password; // allow setting/updating
    private String experienceLevel;
    private String profileImageUrl;
    private List<String> roles; // ROLE_USER, ROLE_ADMIN
}

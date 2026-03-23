package com.hiking.dto;

import lombok.Data;

@Data
public class UpdateProfileRequestDTO {
    private String firstName;
    private String lastName;
    private Integer age;
    private String city;
    private String experienceLevel;
}

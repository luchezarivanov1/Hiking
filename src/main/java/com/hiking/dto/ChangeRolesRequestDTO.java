package com.hiking.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChangeRolesRequestDTO {

    @NotEmpty(message = "Roles list must not be empty")
    private List<String> roles;
}

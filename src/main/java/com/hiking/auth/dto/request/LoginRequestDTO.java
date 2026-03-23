package com.hiking.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @JsonAlias("email")
    private String username;
    private String password;
}

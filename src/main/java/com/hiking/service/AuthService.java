package com.hiking.service;

import com.hiking.dto.LoginRequestDTO;
import com.hiking.dto.RegisterRequestDTO;
import com.hiking.dto.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}

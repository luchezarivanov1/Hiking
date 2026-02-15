package com.hiking.auth.service;

import com.hiking.auth.dto.request.LoginRequestDTO;
import com.hiking.auth.dto.request.RegisterRequestDTO;
import com.hiking.auth.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}

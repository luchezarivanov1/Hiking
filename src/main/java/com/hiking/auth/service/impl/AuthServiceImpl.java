package com.hiking.auth.service.impl;

import com.hiking.auth.dto.request.LoginRequestDTO;
import com.hiking.auth.dto.request.RegisterRequestDTO;
import com.hiking.auth.dto.response.AuthResponseDTO;
import com.hiking.auth.service.AuthService;
import com.hiking.security.CustomUserDetails;
import com.hiking.security.JwtService;
import com.hiking.entity.Role;
import com.hiking.entity.RoleType;
import com.hiking.entity.User;
import com.hiking.repository.RoleRepository;
import com.hiking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) {

        Role userRole = roleRepository.findByName(RoleType.USER.name())
                .orElseThrow();

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(userRole);

        userRepository.save(user);

        String token = jwtService.generateToken(
                new CustomUserDetails(user));

        return new AuthResponseDTO(token, user.getUsername());
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        User user = userRepository.findByUsername(
                request.getUsername()).orElseGet(() -> 
                userRepository.findByEmail(request.getUsername()).orElseThrow());

        String token = jwtService.generateToken(
                new CustomUserDetails(user));

        return new AuthResponseDTO(token, user.getUsername());
    }
}

package com.hiking.auth.service.impl;

import com.hiking.auth.dto.request.LoginRequestDTO;
import com.hiking.auth.dto.request.RegisterRequestDTO;
import com.hiking.auth.dto.response.AuthResponseDTO;
import com.hiking.auth.service.AuthService;
import com.hiking.exception.BadRequestException;
import com.hiking.security.CustomUserDetails;
import com.hiking.security.JwtService;
import com.hiking.entity.Role;
import com.hiking.entity.RoleType;
import com.hiking.entity.User;
import com.hiking.repository.RoleRepository;
import com.hiking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        Role userRole = roleRepository.findByName(RoleType.USER.name())
                .orElseThrow();

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(userRole);

        userRepository.save(user);

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return new AuthResponseDTO(token, user.getUsername());
    }

    @Override
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseGet(() -> userRepository.findByEmail(request.getUsername())
                        .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials")));

        if (user.isAccountLocked()) {
            throw new LockedException("Account is locked due to too many failed login attempts. Please contact support.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLocked(true);
                userRepository.save(user);
                throw new LockedException("Account has been locked after " + MAX_FAILED_ATTEMPTS + " failed login attempts. Please contact support.");
            }
            userRepository.save(user);
            throw e;
        }

        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return new AuthResponseDTO(token, user.getUsername());
    }
}

package com.hiking.auth.service.impl;

import com.hiking.dto.LoginRequestDTO;
import com.hiking.dto.RegisterRequestDTO;
import com.hiking.dto.AuthResponseDTO;
import com.hiking.entity.Role;
import com.hiking.entity.RoleType;
import com.hiking.entity.User;
import com.hiking.exception.BadRequestException;
import com.hiking.repository.RoleRepository;
import com.hiking.repository.UserRepository;
import com.hiking.security.JwtService;
import com.hiking.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("hiker");
        registerRequest.setEmail("hiker@example.com");
        registerRequest.setPassword("Passw0rd!");

        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("hiker");
        loginRequest.setPassword("Passw0rd!");
    }

    private User existingUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("hiker");
        u.setEmail("hiker@example.com");
        u.setPassword("hashed");
        return u;
    }

    // ---- register ----

    @Test
    void register_newUser_savesAndReturnsToken() {
        when(userRepository.existsByEmail("hiker@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("hiker")).thenReturn(false);
        when(roleRepository.findByName(RoleType.USER.name())).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode("Passw0rd!")).thenReturn("hashed");
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponseDTO response = authService.register(registerRequest);

        assertEquals("jwt-token", response.getToken());
        assertEquals("hiker", response.getUsername());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hashed", captor.getValue().getPassword());
        assertEquals("hiker@example.com", captor.getValue().getEmail());
    }

    @Test
    void register_emailAlreadyRegistered_throws() {
        when(userRepository.existsByEmail("hiker@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_usernameTaken_throws() {
        when(userRepository.existsByEmail("hiker@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("hiker")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    // ---- login ----

    @Test
    void login_validCredentials_resetsAttemptsAndReturnsToken() {
        User user = existingUser();
        user.setFailedLoginAttempts(3);
        when(userRepository.findByUsername("hiker")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponseDTO response = authService.login(loginRequest);

        assertEquals("jwt-token", response.getToken());
        assertEquals(0, user.getFailedLoginAttempts());
        verify(userRepository).save(user);
    }

    @Test
    void login_resolvesUserByEmailWhenUsernameLookupFails() {
        User user = existingUser();
        loginRequest.setUsername("hiker@example.com");
        when(userRepository.findByUsername("hiker@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("hiker@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponseDTO response = authService.login(loginRequest);

        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_unknownUser_throwsUsernameNotFound() {
        when(userRepository.findByUsername("hiker")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("hiker")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_lockedAccount_throwsLocked() {
        User user = existingUser();
        user.setAccountLocked(true);
        when(userRepository.findByUsername("hiker")).thenReturn(Optional.of(user));

        assertThrows(LockedException.class, () -> authService.login(loginRequest));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_badCredentials_incrementsAttemptsAndRethrows() {
        User user = existingUser();
        user.setFailedLoginAttempts(1);
        when(userRepository.findByUsername("hiker")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        assertEquals(2, user.getFailedLoginAttempts());
        assertFalse(user.isAccountLocked());
        verify(userRepository).save(user);
    }

    @Test
    void login_fifthBadCredential_locksAccount() {
        User user = existingUser();
        user.setFailedLoginAttempts(4); // next failure is the 5th
        when(userRepository.findByUsername("hiker")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        assertThrows(LockedException.class, () -> authService.login(loginRequest));
        assertTrue(user.isAccountLocked());
        assertEquals(5, user.getFailedLoginAttempts());
        verify(userRepository).save(user);
    }

    @Test
    void login_successfulAuthentication_invokesAuthenticationManager() {
        User user = existingUser();
        when(userRepository.findByUsername("hiker")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        authService.login(loginRequest);

        verify(authenticationManager).authenticate(any());
    }
}

package com.hiking.user.controller;

import com.hiking.user.dto.UserDTO;
import com.hiking.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Get current user profile
    @GetMapping("/me")
    public UserDTO getProfile(Authentication authentication) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userService.getUserByEmail(email);
    }

    // Create or update user (ADMIN can update any, user can update self)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO createUser(@RequestBody UserDTO dto) {
        return userService.saveOrUpdateUser(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        dto.setId(id);
        return userService.saveOrUpdateUser(dto);
    }

    // Get all users (ADMIN only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // Delete user (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

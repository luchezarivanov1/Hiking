package com.hiking.controller;

import com.hiking.dto.ChangePasswordRequestDTO;
import com.hiking.dto.ChangeRolesRequestDTO;
import com.hiking.dto.UpdateProfileRequestDTO;
import com.hiking.dto.UserDTO;
import com.hiking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // Update current user's own profile fields
    @PutMapping("/me")
    public UserDTO updateProfile(@RequestBody UpdateProfileRequestDTO request, Authentication authentication) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userService.updateCurrentUserProfile(email, request);
    }

    // Upload avatar for the current user
    @PostMapping("/me/avatar")
    public ResponseEntity<UserDTO> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return ResponseEntity.ok(userService.uploadAvatar(email, file));
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

    // Get user by ID (any authenticated user)
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Search users by username, first name, or last name (excludes the caller)
    @GetMapping("/search")
    public List<UserDTO> searchUsers(@RequestParam String q, Authentication authentication) {
        String callerEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userService.searchUsers(q, callerEmail);
    }

    // Get all users (ADMIN only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // Change password — accessible by the user themselves or an ADMIN
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequestDTO request,
            Authentication authentication) {
        userService.changePassword(id, request, authentication);
        return ResponseEntity.noContent().build();
    }

    // Change user roles (ADMIN only)
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> changeUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody ChangeRolesRequestDTO request) {
        return ResponseEntity.ok(userService.changeUserRoles(id, request));
    }

    // Admin edit another user's profile fields (no username/email)
    @PutMapping("/{id}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> adminUpdateUserProfile(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequestDTO request) {
        return ResponseEntity.ok(userService.adminUpdateUserProfile(id, request));
    }

    // Deactivate user (ADMIN only)
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    // Activate user (ADMIN only)
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    // Delete user (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

package com.hiking.service;

import com.hiking.dto.ChangePasswordRequestDTO;
import com.hiking.dto.ChangeRolesRequestDTO;
import com.hiking.dto.UserDTO;
import com.hiking.entity.Role;
import com.hiking.entity.RoleType;
import com.hiking.entity.User;
import com.hiking.exception.BadRequestException;
import com.hiking.exception.ResourceNotFoundException;
import com.hiking.exception.UnauthorizedException;
import com.hiking.repository.RoleRepository;
import com.hiking.repository.UserRepository;
import com.hiking.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    // Create or update a user
    public UserDTO saveOrUpdateUser(UserDTO dto) {
        User user;
        if (dto.getId() != null) {
            user = userRepo.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = new User();
        }

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setExperienceLevel(dto.getExperienceLevel());
        user.setProfileImageUrl(dto.getProfileImageUrl());
        user.setTotalDistanceKm(dto.getTotalDistanceKm());
        user.setTotalHikesCompleted(dto.getTotalHikesCompleted());

        if (dto.getRoles() != null) {
            List<Role> roles = dto.getRoles().stream()
                    .map(name -> roleRepo.findByName(name)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + name)))
                    .collect(Collectors.toList());
            user.setRoles(roles);
        }

        User saved = userRepo.save(user);
        return mapToDTO(saved);
    }

    // Get user by email
    public UserDTO getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    // Get user by id
    public UserDTO getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    // Search users by username or email
    public List<UserDTO> searchUsers(String query) {
        return userRepo.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // List all users (ADMIN only)
    public List<UserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        if (user.getFriends() != null) {
            dto.setFriendIds(user.getFriends().stream().map(User::getId).collect(Collectors.toList()));
        }
        return dto;
    }

    // Change roles for a user (ADMIN only)
    @Transactional
    public UserDTO changeUserRoles(Long userId, ChangeRolesRequestDTO request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Role> roles = request.getRoles().stream()
                .map(roleName -> {
                    try {
                        RoleType.valueOf(roleName);
                    } catch (IllegalArgumentException e) {
                        throw new BadRequestException("Invalid role: " + roleName);
                    }
                    return roleRepo.findByName(roleName)
                            .orElseThrow(() -> new BadRequestException("Invalid role: " + roleName));
                })
                .collect(Collectors.toList());

        user.setRoles(roles);
        return mapToDTO(userRepo.save(user));
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDTO request, Authentication authentication) {
        User target = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        CustomUserDetails caller = (CustomUserDetails) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isSelf = caller.getUsername().equals(target.getEmail());

        if (!isAdmin && !isSelf) {
            throw new UnauthorizedException("You are not authorized to change this user's password");
        }

        if (!isAdmin) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
                throw new BadRequestException("Current password is required");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), target.getPassword())) {
                throw new BadRequestException("Current password is incorrect");
            }
        }

        target.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(target);
    }

    // Delete user (ADMIN only)
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}

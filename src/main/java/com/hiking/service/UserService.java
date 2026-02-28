package com.hiking.service;

import com.hiking.dto.UserDTO;
import com.hiking.entity.Role;
import com.hiking.entity.User;
import com.hiking.repository.RoleRepository;
import com.hiking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        // Note: Password update logic might need more care with the new DTO which doesn't have password field
        // But for now keeping it simple as per original
        
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

    // Delete user (ADMIN only)
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}

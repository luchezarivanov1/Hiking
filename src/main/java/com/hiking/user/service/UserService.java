package com.hiking.user.service;

import com.hiking.user.dto.UserDTO;
import com.hiking.user.entity.Role;
import com.hiking.user.entity.User;
import com.hiking.user.repository.RoleRepository;
import com.hiking.user.repository.UserRepository;
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
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setExperienceLevel(dto.getExperienceLevel());
        user.setProfileImageUrl(dto.getProfileImageUrl());

        if (dto.getRoles() != null) {
            List<Role> roles = dto.getRoles().stream()
                    .map(name -> roleRepo.findByName(name)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + name)))
                    .collect(Collectors.toList());
            user.setRoles(roles);
        }

        User saved = userRepo.save(user);
        UserDTO result = modelMapper.map(saved, UserDTO.class);
        result.setRoles(saved.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return result;
    }

    // Get user by email
    public UserDTO getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return dto;
    }

    // List all users (ADMIN only)
    public List<UserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(user -> {
                    UserDTO dto = modelMapper.map(user, UserDTO.class);
                    dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Delete user (ADMIN only)
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}

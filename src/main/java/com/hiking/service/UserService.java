package com.hiking.service;

import com.hiking.dto.ChangePasswordRequestDTO;
import com.hiking.dto.ChangeRolesRequestDTO;
import com.hiking.dto.UpdateProfileRequestDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;

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

    // Search users by username, first name, or last name (min 3 chars), excluding the caller
    public List<UserDTO> searchUsers(String query, String callerEmail) {
        if (query == null || query.length() < 3) {
            return List.of();
        }
        return userRepo.searchByUsernameOrName(query).stream()
                .filter(u -> !u.getEmail().equalsIgnoreCase(callerEmail))
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
        dto.setFollowerCount(user.getFollowers() == null ? 0 : user.getFollowers().size());
        dto.setFollowingCount(user.getFollowing() == null ? 0 : user.getFollowing().size());
        dto.setFollowedByMe(currentUserFollows(user));
        dto.setAccountLocked(user.isAccountLocked());
        return dto;
    }

    private boolean currentUserFollows(User target) {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails details)) return false;
        if (target.getFollowers() == null) return false;
        String email = details.getUsername();
        return target.getFollowers().stream().anyMatch(u -> email.equals(u.getEmail()));
    }

    @Transactional
    public void follow(Long targetUserId) {
        User me = currentUserOrThrow();
        if (me.getId().equals(targetUserId)) {
            throw new BadRequestException("You cannot follow yourself");
        }
        User target = userRepo.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (me.getFollowing().stream().noneMatch(u -> u.getId().equals(target.getId()))) {
            me.getFollowing().add(target);
            userRepo.save(me);
        }
    }

    @Transactional
    public void unfollow(Long targetUserId) {
        User me = currentUserOrThrow();
        me.getFollowing().removeIf(u -> u.getId().equals(targetUserId));
        userRepo.save(me);
    }

    public List<UserDTO> getFollowers(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<User> followers = user.getFollowers();
        if (followers == null) return List.of();
        return followers.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<UserDTO> getFollowing(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<User> following = user.getFollowing();
        if (following == null) return List.of();
        return following.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private User currentUserOrThrow() {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails details)) {
            throw new UnauthorizedException("Not authenticated");
        }
        return userRepo.findByEmail(details.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Update current user's own profile fields
    @Transactional
    public UserDTO updateCurrentUserProfile(String email, UpdateProfileRequestDTO request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAge(request.getAge());
        user.setCity(request.getCity());
        if (request.getExperienceLevel() != null) {
            user.setExperienceLevel(request.getExperienceLevel());
        }
        return mapToDTO(userRepo.save(user));
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

    // Admin edit another user's profile (no username/email changes)
    @Transactional
    public UserDTO adminUpdateUserProfile(Long id, UpdateProfileRequestDTO request) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAge(request.getAge());
        user.setCity(request.getCity());
        if (request.getExperienceLevel() != null) {
            user.setExperienceLevel(request.getExperienceLevel());
        } else {
            user.setExperienceLevel(null);
        }
        return mapToDTO(userRepo.save(user));
    }

    // Deactivate user — locks the account so they cannot log in (ADMIN only)
    @Transactional
    public UserDTO deactivateUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setAccountLocked(true);
        return mapToDTO(userRepo.save(user));
    }

    // Activate user — unlocks the account and resets failed attempts (ADMIN only)
    @Transactional
    public UserDTO activateUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        return mapToDTO(userRepo.save(user));
    }

    // Upload and set avatar for the current user
    @Transactional
    public UserDTO uploadAvatar(String email, MultipartFile file) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String url = fileStorageService.store(file, "avatars");
        user.setProfileImageUrl(url);
        return mapToDTO(userRepo.save(user));
    }

    // Delete user (ADMIN only)
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}

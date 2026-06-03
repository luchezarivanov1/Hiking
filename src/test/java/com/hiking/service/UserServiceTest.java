package com.hiking.service;

import com.hiking.dto.ChangePasswordRequestDTO;
import com.hiking.dto.ChangeRolesRequestDTO;
import com.hiking.dto.UpdateProfileRequestDTO;
import com.hiking.dto.UserDTO;
import com.hiking.entity.Role;
import com.hiking.entity.User;
import com.hiking.exception.BadRequestException;
import com.hiking.exception.ResourceNotFoundException;
import com.hiking.exception.UnauthorizedException;
import com.hiking.repository.RoleRepository;
import com.hiking.repository.UserRepository;
import com.hiking.support.SecurityContextTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private RoleRepository roleRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRoles(new ArrayList<>(List.of(role)));

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setRoles(Collections.singletonList("ROLE_USER"));
    }

    @AfterEach
    void tearDown() {
        SecurityContextTestUtils.clear();
    }

    /** Each mapToDTO call returns a fresh DTO; the service then overwrites roles/counts on it. */
    private void stubMapper() {
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenAnswer(inv -> new UserDTO());
    }

    @Test
    void getUserById_success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        stubMapper();

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(List.of("ROLE_USER"), result.getRoles());
        verify(userRepo).findById(1L);
    }

    @Test
    void getUserById_notFound_throws() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getUserByEmail_notFound_throws() {
        when(userRepo.findByEmail("x@y.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserByEmail("x@y.com"));
    }

    @Test
    void saveOrUpdateUser_update_resolvesRolesAndSaves() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        stubMapper();

        userDTO.setRoles(List.of("ROLE_USER"));
        UserDTO result = userService.saveOrUpdateUser(userDTO);

        assertNotNull(result);
        verify(userRepo).save(any(User.class));
    }

    @Test
    void saveOrUpdateUser_create_whenIdNull() {
        userDTO.setId(null);
        userDTO.setRoles(null);
        when(userRepo.save(any(User.class))).thenReturn(user);
        stubMapper();

        userService.saveOrUpdateUser(userDTO);

        verify(userRepo, never()).findById(any());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void getAllUsers_returnsMappedList() {
        when(userRepo.findAll()).thenReturn(List.of(user));
        stubMapper();

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void deleteUser_delegates() {
        userService.deleteUser(1L);
        verify(userRepo).deleteById(1L);
    }

    // ---- search ----

    @Test
    void searchUsers_shortQuery_returnsEmpty() {
        assertTrue(userService.searchUsers("ab", "me@example.com").isEmpty());
        verifyNoInteractions(userRepo);
    }

    @Test
    void searchUsers_nullQuery_returnsEmpty() {
        assertTrue(userService.searchUsers(null, "me@example.com").isEmpty());
    }

    @Test
    void searchUsers_excludesCaller() {
        User other = new User();
        other.setId(2L);
        other.setEmail("other@example.com");
        other.setRoles(new ArrayList<>());
        when(userRepo.searchByUsernameOrName("test")).thenReturn(List.of(user, other));
        stubMapper();

        List<UserDTO> result = userService.searchUsers("test", "test@example.com");

        // caller (test@example.com) filtered out, only "other" remains
        assertEquals(1, result.size());
    }

    // ---- follow / unfollow ----

    @Test
    void follow_self_throwsBadRequest() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> userService.follow(1L));
    }

    @Test
    void follow_notAuthenticated_throwsUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> userService.follow(2L));
    }

    @Test
    void follow_newTarget_addsAndSaves() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        User target = new User();
        target.setId(2L);
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepo.findById(2L)).thenReturn(Optional.of(target));

        userService.follow(2L);

        assertTrue(user.getFollowing().stream().anyMatch(u -> u.getId().equals(2L)));
        verify(userRepo).save(user);
    }

    @Test
    void follow_alreadyFollowing_doesNotSaveAgain() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        User target = new User();
        target.setId(2L);
        user.setFollowing(new ArrayList<>(List.of(target)));
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepo.findById(2L)).thenReturn(Optional.of(target));

        userService.follow(2L);

        verify(userRepo, never()).save(any());
    }

    @Test
    void unfollow_removesTargetAndSaves() {
        SecurityContextTestUtils.authenticate(user, "ROLE_USER");
        User target = new User();
        target.setId(2L);
        user.setFollowing(new ArrayList<>(List.of(target)));
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        userService.unfollow(2L);

        assertTrue(user.getFollowing().isEmpty());
        verify(userRepo).save(user);
    }

    @Test
    void getFollowers_userNotFound_throws() {
        when(userRepo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getFollowers(9L));
    }

    // ---- profile updates ----

    @Test
    void updateCurrentUserProfile_setsFields() {
        UpdateProfileRequestDTO req = new UpdateProfileRequestDTO();
        req.setFirstName("Ivan");
        req.setLastName("Petrov");
        req.setAge(30);
        req.setCity("Sofia");
        req.setExperienceLevel("Advanced");
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);
        stubMapper();

        userService.updateCurrentUserProfile("test@example.com", req);

        assertEquals("Ivan", user.getFirstName());
        assertEquals("Sofia", user.getCity());
        assertEquals("Advanced", user.getExperienceLevel());
    }

    @Test
    void adminUpdateUserProfile_nullExperience_clearsIt() {
        user.setExperienceLevel("Advanced");
        UpdateProfileRequestDTO req = new UpdateProfileRequestDTO();
        req.setExperienceLevel(null);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);
        stubMapper();

        userService.adminUpdateUserProfile(1L, req);

        assertNull(user.getExperienceLevel());
    }

    // ---- roles ----

    @Test
    void changeUserRoles_validRoles_updates() {
        ChangeRolesRequestDTO req = new ChangeRolesRequestDTO();
        req.setRoles(List.of("ADMIN"));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepo.save(user)).thenReturn(user);
        stubMapper();

        userService.changeUserRoles(1L, req);

        assertEquals(1, user.getRoles().size());
        assertEquals("ADMIN", user.getRoles().get(0).getName());
    }

    @Test
    void changeUserRoles_invalidRoleName_throws() {
        ChangeRolesRequestDTO req = new ChangeRolesRequestDTO();
        req.setRoles(List.of("SUPERHERO"));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> userService.changeUserRoles(1L, req));
    }

    // ---- password ----

    @Test
    void changePassword_selfWithCorrectCurrent_succeeds() {
        user.setPassword("oldHash");
        ChangePasswordRequestDTO req = new ChangePasswordRequestDTO();
        req.setCurrentPassword("oldPlain");
        req.setNewPassword("NewPassw0rd!");
        Authentication auth = authFor(user, "ROLE_USER");
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPlain", "oldHash")).thenReturn(true);
        when(passwordEncoder.encode("NewPassw0rd!")).thenReturn("newHash");

        userService.changePassword(1L, req, auth);

        assertEquals("newHash", user.getPassword());
        verify(userRepo).save(user);
    }

    @Test
    void changePassword_selfWrongCurrent_throws() {
        user.setPassword("oldHash");
        ChangePasswordRequestDTO req = new ChangePasswordRequestDTO();
        req.setCurrentPassword("wrong");
        req.setNewPassword("NewPassw0rd!");
        Authentication auth = authFor(user, "ROLE_USER");
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "oldHash")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userService.changePassword(1L, req, auth));
    }

    @Test
    void changePassword_adminSkipsCurrentPasswordCheck() {
        User target = new User();
        target.setId(2L);
        target.setEmail("target@example.com");
        target.setRoles(new ArrayList<>());
        ChangePasswordRequestDTO req = new ChangePasswordRequestDTO();
        req.setNewPassword("NewPassw0rd!");
        Authentication adminAuth = authFor(user, "ROLE_ADMIN");
        when(userRepo.findById(2L)).thenReturn(Optional.of(target));
        when(passwordEncoder.encode("NewPassw0rd!")).thenReturn("newHash");

        userService.changePassword(2L, req, adminAuth);

        assertEquals("newHash", target.getPassword());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void changePassword_otherUserNonAdmin_throwsUnauthorized() {
        User target = new User();
        target.setId(2L);
        target.setEmail("target@example.com");
        ChangePasswordRequestDTO req = new ChangePasswordRequestDTO();
        req.setNewPassword("NewPassw0rd!");
        Authentication auth = authFor(user, "ROLE_USER");
        when(userRepo.findById(2L)).thenReturn(Optional.of(target));

        assertThrows(UnauthorizedException.class, () -> userService.changePassword(2L, req, auth));
    }

    // ---- activation ----

    @Test
    void deactivateUser_locksAccount() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);
        stubMapper();

        userService.deactivateUser(1L);

        assertTrue(user.isAccountLocked());
    }

    @Test
    void activateUser_unlocksAndResetsAttempts() {
        user.setAccountLocked(true);
        user.setFailedLoginAttempts(5);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);
        stubMapper();

        userService.activateUser(1L);

        assertFalse(user.isAccountLocked());
        assertEquals(0, user.getFailedLoginAttempts());
    }

    // ---- avatar ----

    @Test
    void uploadAvatar_storesFileAndSetsUrl() {
        MultipartFile file = new MockMultipartFile("f", "a.jpg", "image/jpeg", "x".getBytes());
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(fileStorageService.store(file, "avatars")).thenReturn("http://host/uploads/avatars/a.jpg");
        when(userRepo.save(user)).thenReturn(user);
        stubMapper();

        userService.uploadAvatar("test@example.com", file);

        assertEquals("http://host/uploads/avatars/a.jpg", user.getProfileImageUrl());
    }

    private Authentication authFor(User principalUser, String... authorities) {
        var grantedAuthorities = java.util.Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new).toList();
        return new UsernamePasswordAuthenticationToken(
                new com.hiking.security.CustomUserDetails(principalUser), null, grantedAuthorities);
    }
}

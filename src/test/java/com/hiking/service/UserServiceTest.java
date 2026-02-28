package com.hiking.service;

import com.hiking.dto.UserDTO;
import com.hiking.entity.Role;
import com.hiking.entity.User;
import com.hiking.repository.RoleRepository;
import com.hiking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        user.setRoles(Collections.singletonList(role));

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setRoles(Collections.singletonList("ROLE_USER"));
    }

    @Test
    void testGetUserById_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(userDTO.getUsername(), result.getUsername());
        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testSaveOrUpdateUser_UpdateSuccess() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        UserDTO result = userService.saveOrUpdateUser(userDTO);

        assertNotNull(result);
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testGetAllUsers() {
        when(userRepo.findAll()).thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        List<UserDTO> result = userService.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);
        verify(userRepo, times(1)).deleteById(1L);
    }
}

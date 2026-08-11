package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.request.UpdateUserRequest;
import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.exception.DuplicateEmailException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.mapper.UserMapper;
import com.example.CheckInApp.model.Location;
import com.example.CheckInApp.model.Role;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService CRUD Tests - Service Layer")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequest userRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password123");
        testUser.setLocation(Location.CLUJ);
        testUser.setStatus(true);
        testUser.setRoles(new HashSet<>(Set.of(Role.PARTICIPANT)));

        userRequest = new UserRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setPassword("password123");
        userRequest.setLocation(Location.CLUJ);
        userRequest.setRoles(new HashSet<>(Set.of(Role.PARTICIPANT)));

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFirstName("Johnny");
        updateUserRequest.setLastName("Smith");
        updateUserRequest.setEmail("johnny.smith@example.com");
    }


    @Test
    @DisplayName("test_forAddUser_shouldCreateAndReturnNewUser")
    void test_forAddUser_shouldCreateAndReturnNewUser() {
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toEntity(userRequest)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.addUser(userRequest);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmail(userRequest.getEmail());
        verify(userMapper, times(1)).toEntity(userRequest);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("test_forAddUser_shouldThrowExceptionWhenEmailAlreadyExists")
    void test_forAddUser_shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(DuplicateEmailException.class, () -> userService.addUser(userRequest));
        verify(userRepository, times(1)).findByEmail(userRequest.getEmail());
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("test_forAddUser_shouldSetUserStatusToTrueWhenCreating")
    void test_forAddUser_shouldSetUserStatusToTrueWhenCreating() {
        User newUser = new User();
        newUser.setFirstName("Jane");
        newUser.setLastName("Doe");
        newUser.setEmail("john.doe@example.com");
        newUser.setPassword("password456");
        newUser.setLocation(Location.TIMISOARA);
        newUser.setStatus(true);

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(userRequest)).thenReturn(newUser);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.addUser(userRequest);

        assertTrue(result.isStatus());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("test_forGetUserById_shouldReturnUserWhenIdExists")
    void test_forGetUserById_shouldReturnUserWhenIdExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("test_forGetUserById_shouldThrowExceptionWhenUserNotFound")
    void test_forGetUserById_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("test_forGetAllUsers_shouldReturnPagedUsers")
    void test_forGetAllUsers_shouldReturnPagedUsers() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");
        users.add(user2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(users.size(), result.getContent().size());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("test_forGetAllUsers_shouldReturnEmptyPageWhenNoUsersExist")
    void test_forGetAllUsers_shouldReturnEmptyPageWhenNoUsersExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<User> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(userRepository, times(1)).findAll(pageable);
    }


    @Test
    @DisplayName("test_forUpdateUser_shouldUpdateExistingUserWithNewData")
    void test_forUpdateUser_shouldUpdateExistingUserWithNewData() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("password123");
        updatedUser.setLocation(Location.MURES);
        updatedUser.setStatus(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userMapper).updateEntityFromRequest(testUser, userRequest);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, userRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).updateEntityFromRequest(testUser, userRequest);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("test_forUpdateUser_shouldThrowExceptionWhenUserNotFound")
    void test_forUpdateUser_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(999L, userRequest));
        verify(userRepository, times(1)).findById(999L);
        verify(userMapper, never()).updateEntityFromRequest(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("test_forPatchUser_shouldPartiallyUpdateExistingUser")
    void test_forPatchUser_shouldPartiallyUpdateExistingUser() {
        User patchedUser = new User();
        patchedUser.setId(1L);
        patchedUser.setFirstName("Johnny");
        patchedUser.setLastName("Smith");
        patchedUser.setEmail("johnny.smith@example.com");
        patchedUser.setPassword("password123");
        patchedUser.setLocation(Location.CLUJ);
        patchedUser.setStatus(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userMapper).applyPatch(testUser, updateUserRequest);
        when(userRepository.save(any(User.class))).thenReturn(patchedUser);

        User result = userService.patchUser(1L, updateUserRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).applyPatch(testUser, updateUserRequest);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("test_forPatchUser_shouldThrowExceptionWhenUserNotFound")
    void test_forPatchUser_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.patchUser(999L, updateUserRequest));
        verify(userRepository, times(1)).findById(999L);
        verify(userMapper, never()).applyPatch(any(), any());
        verify(userRepository, never()).save(any());
    }


    @Test
    @DisplayName("test_forDeleteUser_shouldDeleteExistingUser")
    void test_forDeleteUser_shouldDeleteExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("test_forDeleteUser_shouldThrowExceptionWhenUserNotFound")
    void test_forDeleteUser_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(999L));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("test_forDeleteUser_shouldNotThrowExceptionWhenMultipleUsersDeleted")
    void test_forDeleteUser_shouldNotThrowExceptionWhenMultipleUsersDeleted() {
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        doNothing().when(userRepository).delete(any());

        userService.deleteUser(1L);
        userService.deleteUser(2L);

        verify(userRepository, times(2)).delete(any());
    }
}

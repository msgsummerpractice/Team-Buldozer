package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.request.UpdateUserRequest;
import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.dto.response.PagedResponse;
import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.exception.DuplicateEmailException;
import com.example.CheckInApp.exception.ResourceNotFoundException;
import com.example.CheckInApp.mapper.UserMapper;
import com.example.CheckInApp.model.Location;
import com.example.CheckInApp.model.Role;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.service.UserService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController CRUD Tests - Controller Layer")
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserResponse userResponse;
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

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setEmail("john.doe@example.com");
        userResponse.setLocation(Location.CLUJ);
        userResponse.setStatus(true);
        userResponse.setRoles(new HashSet<>(Set.of(Role.PARTICIPANT)));

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
    @DisplayName("test_forAddUser_shouldReturnCreatedStatusWithUserResponse")
    void test_forAddUser_shouldReturnCreatedStatusWithUserResponse() {
        when(userService.addUser(userRequest)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.addUser(userRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponse.getId(), response.getBody().getId());
        assertEquals(userResponse.getEmail(), response.getBody().getEmail());
        verify(userService, times(1)).addUser(userRequest);
        verify(userMapper, times(1)).toResponse(testUser);
    }

    @Test
    @DisplayName("test_forAddUser_shouldReturnValidUserResponseWithAllFields")
    void test_forAddUser_shouldReturnValidUserResponseWithAllFields() {
        when(userService.addUser(userRequest)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.addUser(userRequest);

        UserResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("John", body.getFirstName());
        assertEquals("Doe", body.getLastName());
        assertEquals("john.doe@example.com", body.getEmail());
        assertEquals(Location.CLUJ, body.getLocation());
        assertTrue(body.isStatus());
        verify(userService, times(1)).addUser(userRequest);
    }

    @Test
    @DisplayName("test_forAddUser_shouldThrowDuplicateEmailExceptionWhenEmailExists")
    void test_forAddUser_shouldThrowDuplicateEmailExceptionWhenEmailExists() {
        when(userService.addUser(userRequest)).thenThrow(
                new DuplicateEmailException("A user with email john.doe@example.com already exists")
        );

        assertThrows(DuplicateEmailException.class, () -> userController.addUser(userRequest));
        verify(userService, times(1)).addUser(userRequest);
        verify(userMapper, never()).toResponse(any());
    }


    @Test
    @DisplayName("test_forGetUserById_shouldReturnOkStatusWithUserResponse")
    void test_forGetUserById_shouldReturnOkStatusWithUserResponse() {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponse.getId(), response.getBody().getId());
        assertEquals(userResponse.getEmail(), response.getBody().getEmail());
        verify(userService, times(1)).getUserById(1L);
        verify(userMapper, times(1)).toResponse(testUser);
    }

    @Test
    @DisplayName("test_forGetUserById_shouldReturnUserWithCorrectDetails")
    void test_forGetUserById_shouldReturnUserWithCorrectDetails() {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        UserResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(1L, body.getId());
        assertEquals("John", body.getFirstName());
        assertEquals("Doe", body.getLastName());
        assertEquals("john.doe@example.com", body.getEmail());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("test_forGetUserById_shouldThrowResourceNotFoundExceptionWhenUserNotFound")
    void test_forGetUserById_shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
        when(userService.getUserById(999L)).thenThrow(
                new ResourceNotFoundException("User not found with id 999")
        );

        assertThrows(ResourceNotFoundException.class, () -> userController.getUserById(999L));
        verify(userService, times(1)).getUserById(999L);
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("test_forGetAllUsers_shouldReturnOkStatusWithPagedUserResponse")
    void test_forGetAllUsers_shouldReturnOkStatusWithPagedUserResponse() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setLocation(Location.TIMISOARA);
        user2.setStatus(true);
        users.add(user2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        UserResponse response2 = new UserResponse();
        response2.setId(2L);
        response2.setFirstName("Jane");
        response2.setLastName("Smith");
        response2.setEmail("jane.smith@example.com");
        response2.setLocation(Location.TIMISOARA);
        response2.setStatus(true);

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);
        when(userMapper.toResponse(user2)).thenReturn(response2);

        ResponseEntity<PagedResponse<UserResponse>> response = userController.getAllUsers(0, 10, "id", "asc");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalElements());
        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }

    @Test
    @DisplayName("test_forGetAllUsers_shouldReturnPagedResponseWithCorrectPageNumber")
    void test_forGetAllUsers_shouldReturnPagedResponseWithCorrectPageNumber() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        ResponseEntity<PagedResponse<UserResponse>> response = userController.getAllUsers(0, 10, "id", "asc");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getTotalPages());
        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }

    @Test
    @DisplayName("test_forGetAllUsers_shouldReturnEmptyPageWhenNoUsersExist")
    void test_forGetAllUsers_shouldReturnEmptyPageWhenNoUsersExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(emptyPage);

        ResponseEntity<PagedResponse<UserResponse>> response = userController.getAllUsers(0, 10, "id", "asc");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().isEmpty());
        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }


    @Test
    @DisplayName("test_forUpdateUser_shouldReturnOkStatusWithUpdatedUserResponse")
    void test_forUpdateUser_shouldReturnOkStatusWithUpdatedUserResponse() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("password123");
        updatedUser.setLocation(Location.MURES);
        updatedUser.setStatus(true);

        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(1L);
        updatedResponse.setFirstName("Updated");
        updatedResponse.setLastName("User");
        updatedResponse.setEmail("updated@example.com");
        updatedResponse.setLocation(Location.MURES);
        updatedResponse.setStatus(true);

        when(userService.updateUser(1L, userRequest)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        ResponseEntity<UserResponse> response = userController.updateUser(1L, userRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated", response.getBody().getFirstName());
        assertEquals("updated@example.com", response.getBody().getEmail());
        verify(userService, times(1)).updateUser(1L, userRequest);
        verify(userMapper, times(1)).toResponse(updatedUser);
    }

    @Test
    @DisplayName("test_forUpdateUser_shouldThrowResourceNotFoundExceptionWhenUserDoesNotExist")
    void test_forUpdateUser_shouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        when(userService.updateUser(999L, userRequest)).thenThrow(
                new ResourceNotFoundException("User not found with id 999")
        );

        assertThrows(ResourceNotFoundException.class, () -> userController.updateUser(999L, userRequest));
        verify(userService, times(1)).updateUser(999L, userRequest);
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("test_forPatchUser_shouldReturnOkStatusWithPatchedUserResponse")
    void test_forPatchUser_shouldReturnOkStatusWithPatchedUserResponse() {
        User patchedUser = new User();
        patchedUser.setId(1L);
        patchedUser.setFirstName("Johnny");
        patchedUser.setLastName("Smith");
        patchedUser.setEmail("johnny.smith@example.com");
        patchedUser.setPassword("password123");
        patchedUser.setLocation(Location.CLUJ);
        patchedUser.setStatus(true);

        UserResponse patchedResponse = new UserResponse();
        patchedResponse.setId(1L);
        patchedResponse.setFirstName("Johnny");
        patchedResponse.setLastName("Smith");
        patchedResponse.setEmail("johnny.smith@example.com");
        patchedResponse.setLocation(Location.CLUJ);
        patchedResponse.setStatus(true);

        when(userService.patchUser(1L, updateUserRequest)).thenReturn(patchedUser);
        when(userMapper.toResponse(patchedUser)).thenReturn(patchedResponse);

        ResponseEntity<UserResponse> response = userController.patchUser(1L, updateUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Johnny", response.getBody().getFirstName());
        assertEquals("johnny.smith@example.com", response.getBody().getEmail());
        verify(userService, times(1)).patchUser(1L, updateUserRequest);
        verify(userMapper, times(1)).toResponse(patchedUser);
    }

    @Test
    @DisplayName("test_forPatchUser_shouldThrowResourceNotFoundExceptionWhenUserNotFound")
    void test_forPatchUser_shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
        when(userService.patchUser(999L, updateUserRequest)).thenThrow(
                new ResourceNotFoundException("User not found with id 999")
        );

        assertThrows(ResourceNotFoundException.class, () -> userController.patchUser(999L, updateUserRequest));
        verify(userService, times(1)).patchUser(999L, updateUserRequest);
        verify(userMapper, never()).toResponse(any());
    }


    @Test
    @DisplayName("test_forDeleteUser_shouldReturnNoContentStatus")
    void test_forDeleteUser_shouldReturnNoContentStatus() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("test_forDeleteUser_shouldThrowResourceNotFoundExceptionWhenUserNotFound")
    void test_forDeleteUser_shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
        doThrow(new ResourceNotFoundException("User not found with id 999")).when(userService).deleteUser(999L);

        assertThrows(ResourceNotFoundException.class, () -> userController.deleteUser(999L));
        verify(userService, times(1)).deleteUser(999L);
    }

    @Test
    @DisplayName("test_forDeleteUser_shouldCallServiceDeleteMethodOnce")
    void test_forDeleteUser_shouldCallServiceDeleteMethodOnce() {
        doNothing().when(userService).deleteUser(1L);

        userController.deleteUser(1L);

        verify(userService, times(1)).deleteUser(1L);
    }
}

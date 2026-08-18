package com.example.CheckInApp.controller;

import com.example.CheckInApp.dto.request.UserProfileRequest;
import com.example.CheckInApp.dto.request.UserRequestByAdmin;
import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.model.UserLocation;
import com.example.CheckInApp.model.UserRole;
import com.example.CheckInApp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllUsers_returnsOkWithEmptyList_whenNoUsersExist() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_returnsOkWithUserList_whenUsersExist() throws Exception {
        UserResponse user1 = UserResponse.builder()
                .id(1L).firstName("John").lastName("Doe").email("john@example.com")
                .location(UserLocation.CLUJ).status(true).roles(Set.of(UserRole.PARTICIPANT)).build();
        UserResponse user2 = UserResponse.builder()
                .id(2L).firstName("Jane").lastName("Smith").email("jane@example.com")
                .location(UserLocation.TIMISOARA).status(false).roles(Set.of(UserRole.HR)).build();

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("Jane")))
                .andExpect(jsonPath("$[1].email", is("jane@example.com")));

        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_returnsCorrectHttpStatus() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_returnsOkWithUser_whenUserExists() throws Exception {
        UserResponse user = UserResponse.builder()
                .id(1L).firstName("John").lastName("Doe").email("john@example.com")
                .location(UserLocation.CLUJ).status(true).roles(Set.of(UserRole.PARTICIPANT)).build();

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService).getUserById(1L);
    }

    @Test
    void updateUserProfile_returnsOkWithUpdatedUser_whenValidRequest() throws Exception {
        UserProfileRequest request = UserProfileRequest.builder()
                .firstName("John").lastName("Updated").email("john.updated@example.com")
                .location(UserLocation.CLUJ).build();
        UserResponse updatedUser = UserResponse.builder()
                .id(1L).firstName("John").lastName("Updated").email("john.updated@example.com")
                .location(UserLocation.CLUJ).status(true).roles(Set.of(UserRole.PARTICIPANT)).build();

        when(userService.updateUserProfile(eq(1L), any(UserProfileRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/v1/users/profile/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is("Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")));

        verify(userService).updateUserProfile(eq(1L), any(UserProfileRequest.class));
    }

    @Test
    void updateUserStatusAndRoles_returnsOkWithUpdatedUser_whenValidRequest() throws Exception {
        UserRequestByAdmin request = new UserRequestByAdmin(false, Set.of(UserRole.HR));
        UserResponse updatedUser = UserResponse.builder()
                .id(2L).firstName("Jane").lastName("Smith").email("jane@example.com")
                .location(UserLocation.TIMISOARA).status(false).roles(Set.of(UserRole.HR)).build();

        when(userService.updateUserStatusAndRoles(eq(2L), any(UserRequestByAdmin.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/v1/users/2/status-roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.status", is(false)));

        verify(userService).updateUserStatusAndRoles(eq(2L), any(UserRequestByAdmin.class));
    }
}
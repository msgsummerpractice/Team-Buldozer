// package com.example.CheckInApp.controller;

// import com.example.CheckInApp.dto.response.UserResponse;
// import com.example.CheckInApp.model.UserLocation;
// import com.example.CheckInApp.model.UserRole;
// import com.example.CheckInApp.service.UserService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import java.util.List;
// import java.util.Set;

// import static org.hamcrest.Matchers.*;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @ExtendWith(MockitoExtension.class)
// class UserControllerTest {

//     private MockMvc mockMvc;

//     @Mock
//     private UserService userService;

//     @InjectMocks
//     private UserController userController;

//     @BeforeEach
//     void setUp() {
//         mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//     }

//     @Test
//     void getAllUsers_returnsOkWithEmptyList_whenNoUsersExist() throws Exception {
//         when(userService.getAllUsers()).thenReturn(List.of());

//         mockMvc.perform(get("/api/v1/users"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType("application/json"))
//                 .andExpect(jsonPath("$", hasSize(0)));

//         verify(userService).getAllUsers();
//     }

//     @Test
//     void getAllUsers_returnsOkWithUserList_whenUsersExist() throws Exception {
//         UserResponse user1 = UserResponse.builder()
//                 .id(1L).firstName("John").lastName("Doe").email("john@example.com")
//                 .location(UserLocation.CLUJ).status(true).userRoles(Set.of(UserRole.PARTICIPANT)).build();
//         UserResponse user2 = UserResponse.builder()
//                 .id(2L).firstName("Jane").lastName("Smith").email("jane@example.com")
//                 .location(UserLocation.TIMISOARA).status(false).userRoles(Set.of(UserRole.HR)).build();

//         when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

//         mockMvc.perform(get("/api/v1/users"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType("application/json"))
//                 .andExpect(jsonPath("$", hasSize(2)))
//                 .andExpect(jsonPath("$[0].id", is(1)))
//                 .andExpect(jsonPath("$[0].firstName", is("John")))
//                 .andExpect(jsonPath("$[0].email", is("john@example.com")))
//                 .andExpect(jsonPath("$[1].id", is(2)))
//                 .andExpect(jsonPath("$[1].firstName", is("Jane")))
//                 .andExpect(jsonPath("$[1].email", is("jane@example.com")));

//         verify(userService).getAllUsers();
//     }

//     @Test
//     void getAllUsers_returnsCorrectHttpStatus() throws Exception {
//         when(userService.getAllUsers()).thenReturn(List.of());

//         mockMvc.perform(get("/api/v1/users"))
//                 .andExpect(status().isOk());
//     }
// }

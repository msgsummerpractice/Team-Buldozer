package com.example.CheckInApp;


import com.example.CheckInApp.service.RegisterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.CheckInApp.controller.RegisterController;
import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.exception.DuplicateEmailException;
import com.example.CheckInApp.service.JwtUtil;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;


@WebMvcTest(RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.flyway.enabled=false",
})
public class AuthenticationControllerEndpointsTests1{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterService registerService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
void testRegisterUserSuccess() throws Exception {
     String validJson = "{"
            + "\"email\":\"test@example.com\","
            + "\"password\":\"Parola123!\","
            + "\"firstName\":\"John\","
            + "\"lastName\":\"Doe\","
            + "\"location\":\"CLUJ\""
            + "}";

    mockMvc.perform(post("/api/register")
            .contentType("application/json")
            .content(validJson))
            .andExpect(status().isCreated());
}

@Test
void testRegisterUserDuplicateEmail() throws Exception {
    String validJson = "{"
            + "\"email\":\"test@example.com\","
            + "\"password\":\"Parola123!\","
            + "\"firstName\":\"John\","
            + "\"lastName\":\"Doe\","
            + "\"location\":\"CLUJ\""
            + "}";
            doThrow(new DuplicateEmailException("Email-ul există deja!"))
            .when(registerService).registerUser(org.mockito.ArgumentMatchers.any());

    mockMvc.perform(post("/api/register")
            .contentType("application/json")
            .content(validJson))
            .andExpect(status().isConflict()); 
}

    @Test
    void testRegisterUserInvalidInput() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("invalid-email");
        userRequest.setPassword("password");
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");

        mockMvc.perform(post("/api/register")
                .contentType("application/json")
                .content("{\"email\":\"invalid-email\",\"password\":\"password\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isBadRequest());
                 }
                }
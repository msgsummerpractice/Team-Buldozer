package com.example.CheckInApp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.CheckInApp.exception.DuplicateEmailException;
import com.example.CheckInApp.repository.UserRepository;
import com.example.CheckInApp.security.AuthController;
import com.example.CheckInApp.security.AuthService;
import com.example.CheckInApp.security.JwtUtil;
import com.example.CheckInApp.service.PasswordResetService;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.flyway.enabled=false",
})
public class AuthenticationControllerEndpointsTests {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthService registerService;

        @MockitoBean
        private PasswordResetService passwordResetService;

        @MockitoBean
        private JwtUtil jwtUtil;

        @MockitoBean
        private UserDetailsService userDetailsService;

        @MockitoBean
        private UserRepository userRepository;

        @Test
        void testRegisterUserSuccess() throws Exception {
                String validJson = "{"
                                + "\"email\":\"test@example.com\","
                                + "\"password\":\"Parola123!\","
                                + "\"firstName\":\"John\","
                                + "\"lastName\":\"Doe\","
                                + "\"location\":\"CLUJ\""
                                + "}";

                mockMvc.perform(post("/api/v1/auth/register")
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
                                .when(registerService)
                                .registerUser(org.mockito.ArgumentMatchers.any());

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType("application/json")
                                .content(validJson))
                                .andExpect(status().isConflict());
        }

        @Test
        void testRegisterUserInvalidInput() throws Exception {
                String invalidJson = "{"
                                + "\"email\":\"invalid-email\","
                                + "\"password\":\"short\","
                                + "\"firstName\":\"John\","
                                + "\"lastName\":\"Doe\","
                                + "\"location\":\"CLUJ\""
                                + "}";

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType("application/json")
                                .content(invalidJson))
                                .andExpect(status().isBadRequest());
        }
}
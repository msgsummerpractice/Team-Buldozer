// package com.example.CheckInApp.service;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.test.context.ActiveProfiles;

// import java.util.ArrayList;
// import java.util.Date;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// @ActiveProfiles("test")
// class JwtUtilTest {

//     @Autowired
//     private JwtUtil jwtUtil;

//     private UserDetails testUser;

//     @BeforeEach
//     void setUp() {
//         testUser = User.builder()
//                 .username("testuser")
//                 .password("password")
//                 .authorities(new ArrayList<>())
//                 .build();
//     }

//     @Test
//     void testGenerateToken() {
//         String token = jwtUtil.generateToken("testuser");
//         assertNotNull(token);
//         assertFalse(token.isEmpty());
//     }

//     @Test
//     void testExtractUsername() {
//         String token = jwtUtil.generateToken("testuser");
//         String username = jwtUtil.extractUsername(token);
//         assertEquals("testuser", username);
//     }

//     @Test
//     void testExtractExpiration() {
//         String token = jwtUtil.generateToken("testuser");
//         Date expiration = jwtUtil.extractExpiration(token);
//         assertNotNull(expiration);
//         assertTrue(expiration.after(new Date()));
//     }

//     @Test
//     void testValidateToken() {
//         String token = jwtUtil.generateToken("testuser");
//         assertTrue(jwtUtil.validateToken(token));
//     }

//     @Test
//     void testValidateTokenWithUserDetails() {
//         String token = jwtUtil.generateToken("testuser");
//         assertTrue(jwtUtil.validateToken(token, testUser));
//     }

//     @Test
//     void testValidateTokenWithWrongUser() {
//         String token = jwtUtil.generateToken("testuser");
//         UserDetails wrongUser = User.builder()
//                 .username("wronguser")
//                 .password("password")
//                 .authorities(new ArrayList<>())
//                 .build();
//         assertFalse(jwtUtil.validateToken(token, wrongUser));
//     }

//     @Test
//     void testInvalidToken() {
//         String invalidToken = "invalid.token.here";
//         assertFalse(jwtUtil.validateToken(invalidToken));
//     }

//     @Test
//     void testGetExpirationTime() {
//         long expirationTime = jwtUtil.getExpirationTime();
//         assertTrue(expirationTime > 0);
//     }
// }

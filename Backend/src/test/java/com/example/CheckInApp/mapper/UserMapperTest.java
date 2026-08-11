package com.example.CheckInApp.mapper;

import com.example.CheckInApp.dto.request.UpdateUserRequest;
import com.example.CheckInApp.dto.request.UserRequest;
import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.model.Location;
import com.example.CheckInApp.model.Role;
import com.example.CheckInApp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserMapper CRUD Tests - Mapping Layer")
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    @DisplayName("test_forToEntity_shouldConvertUserRequestToUserEntity")
    void test_forToEntity_shouldConvertUserRequestToUserEntity() {
        UserRequest request = new UserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        request.setLocation(Location.CLUJ);
        request.setRoles(new HashSet<>(Set.of(Role.PARTICIPANT)));

        User result = userMapper.toEntity(request);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("password123", result.getPassword());
        assertEquals(Location.CLUJ, result.getLocation());
        assertEquals(1, result.getRoles().size());
        assertTrue(result.isStatus());
    }

    @Test
    @DisplayName("test_forToEntity_shouldSetStatusToTrueByDefault")
    void test_forToEntity_shouldSetStatusToTrueByDefault() {
        UserRequest request = new UserRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@example.com");
        request.setPassword("password456");
        request.setLocation(Location.TIMISOARA);
        request.setRoles(new HashSet<>(Set.of(Role.ADMIN)));

        User result = userMapper.toEntity(request);

        assertTrue(result.isStatus(), "User status should be true by default");
    }

    @Test
    @DisplayName("test_forToEntity_shouldHandleNullRequest")
    void test_forToEntity_shouldHandleNullRequest() {
        User result = userMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    @DisplayName("test_forToEntity_shouldHandleNullRoles")
    void test_forToEntity_shouldHandleNullRoles() {
        UserRequest request = new UserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        request.setLocation(Location.CLUJ);
        request.setRoles(null);

        User result = userMapper.toEntity(request);

        assertNotNull(result);
        assertNull(result.getRoles());
    }


    @Test
    @DisplayName("test_forToResponse_shouldConvertUserEntityToUserResponse")
    void test_forToResponse_shouldConvertUserEntityToUserResponse() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setLocation(Location.CLUJ);
        user.setStatus(true);
        user.setRoles(new HashSet<>(Set.of(Role.PARTICIPANT)));

        UserResponse result = userMapper.toResponse(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals(Location.CLUJ, result.getLocation());
        assertTrue(result.isStatus());
        assertEquals(1, result.getRoles().size());
    }

    @Test
    @DisplayName("test_forToResponse_shouldHandleNullUser")
    void test_forToResponse_shouldHandleNullUser() {
        UserResponse result = userMapper.toResponse(null);

        assertNull(result);
    }

    @Test
    @DisplayName("test_forToResponse_shouldPreserveAllUserFields")
    void test_forToResponse_shouldPreserveAllUserFields() {
        User user = new User();
        user.setId(2L);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("jane.smith@example.com");
        user.setLocation(Location.MURES);
        user.setStatus(false);
        user.setRoles(new HashSet<>(Set.of(Role.ADMIN, Role.PARTICIPANT)));

        UserResponse result = userMapper.toResponse(user);

        assertEquals(2L, result.getId());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@example.com", result.getEmail());
        assertEquals(Location.MURES, result.getLocation());
        assertFalse(result.isStatus());
        assertEquals(2, result.getRoles().size());
    }

    @Test
    @DisplayName("test_forUpdateEntityFromRequest_shouldUpdateAllFieldsInUser")
    void test_forUpdateEntityFromRequest_shouldUpdateAllFieldsInUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("oldPassword");
        user.setLocation(Location.CLUJ);
        user.setStatus(true);
        user.setRoles(new HashSet<>(Set.of(Role.PARTICIPANT)));

        UserRequest request = new UserRequest();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setEmail("updated@example.com");
        request.setPassword("newPassword");
        request.setLocation(Location.MURES);
        request.setRoles(new HashSet<>(Set.of(Role.ADMIN)));

        userMapper.updateEntityFromRequest(user, request);

        assertEquals("Updated", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("newPassword", user.getPassword());
        assertEquals(Location.MURES, user.getLocation());
        assertTrue(user.getRoles().contains(Role.ADMIN));
        assertEquals(1L, user.getId());
    }

    @Test
    @DisplayName("test_forUpdateEntityFromRequest_shouldNotUpdateBlankFields")
    void test_forUpdateEntityFromRequest_shouldNotUpdateBlankFields() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        UserRequest request = new UserRequest();
        request.setFirstName("");
        request.setLastName("Smith");
        request.setEmail("  ");
        request.setPassword("newPassword");

        userMapper.updateEntityFromRequest(user, request);

        assertEquals("John", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    @DisplayName("test_forUpdateEntityFromRequest_shouldHandleNullRequest")
    void test_forUpdateEntityFromRequest_shouldHandleNullRequest() {
        User user = new User();
        user.setFirstName("John");
        user.setEmail("john@example.com");

        assertDoesNotThrow(() -> userMapper.updateEntityFromRequest(user, null));
        assertEquals("John", user.getFirstName());
    }

    @Test
    @DisplayName("test_forUpdateEntityFromRequest_shouldHandleEmptyRoles")
    void test_forUpdateEntityFromRequest_shouldHandleEmptyRoles() {
        User user = new User();
        user.setRoles(new HashSet<>(Set.of(Role.PARTICIPANT)));

        UserRequest request = new UserRequest();
        request.setFirstName("John");
        request.setRoles(new HashSet<>());

        userMapper.updateEntityFromRequest(user, request);

        assertTrue(user.getRoles().contains(Role.PARTICIPANT));
    }


    @Test
    @DisplayName("test_forApplyPatch_shouldPartiallyUpdateUserWithProvidedFields")
    void test_forApplyPatch_shouldPartiallyUpdateUserWithProvidedFields() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setLocation(Location.CLUJ);

        UpdateUserRequest patch = new UpdateUserRequest();
        patch.setFirstName("Johnny");
        patch.setLastName("Smith");

        userMapper.applyPatch(user, patch);

        assertEquals("Johnny", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Location.CLUJ, user.getLocation());
    }

    @Test
    @DisplayName("test_forApplyPatch_shouldNotUpdateBlankPatchFields")
    void test_forApplyPatch_shouldNotUpdateBlankPatchFields() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        UpdateUserRequest patch = new UpdateUserRequest();
        patch.setFirstName("");
        patch.setLastName("Smith");
        patch.setEmail("  ");

        userMapper.applyPatch(user, patch);

        assertEquals("John", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    @DisplayName("test_forApplyPatch_shouldHandleNullPatch")
    void test_forApplyPatch_shouldHandleNullPatch() {
        User user = new User();
        user.setFirstName("John");
        user.setEmail("john@example.com");

        assertDoesNotThrow(() -> userMapper.applyPatch(user, null));
        assertEquals("John", user.getFirstName());
    }

    @Test
    @DisplayName("test_forApplyPatch_shouldUpdateOnlyProvidedLocationField")
    void test_forApplyPatch_shouldUpdateOnlyProvidedLocationField() {
        User user = new User();
        user.setFirstName("John");
        user.setLocation(Location.CLUJ);

        UpdateUserRequest patch = new UpdateUserRequest();
        patch.setLocation(Location.TIMISOARA);

        userMapper.applyPatch(user, patch);

        assertEquals("John", user.getFirstName());
        assertEquals(Location.TIMISOARA, user.getLocation());
    }

    @Test
    @DisplayName("test_forApplyPatch_shouldUpdatePasswordInPatch")
    void test_forApplyPatch_shouldUpdatePasswordInPatch() {
        User user = new User();
        user.setPassword("oldPassword");

        UpdateUserRequest patch = new UpdateUserRequest();
        patch.setPassword("newPassword");

        userMapper.applyPatch(user, patch);

        assertEquals("newPassword", user.getPassword());
    }
}

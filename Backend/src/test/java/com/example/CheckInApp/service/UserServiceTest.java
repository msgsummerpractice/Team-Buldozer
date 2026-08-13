package com.example.CheckInApp.service;

import com.example.CheckInApp.dto.response.UserResponse;
import com.example.CheckInApp.mapper.UserMapper;
import com.example.CheckInApp.model.Location;
import com.example.CheckInApp.model.Role;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_returnsEmptyList_whenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).isEmpty();
        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }

    @Test
    void getAllUsers_returnsMappedResponses_whenUsersExist() {
        User user1 = new User(1L, "John", "Doe", "john@example.com", "pass", Location.CLUJ, true, Set.of(Role.PARTICIPANT));
        User user2 = new User(2L, "Jane", "Smith", "jane@example.com", "pass", Location.TIMISOARA, false, Set.of(Role.HR));

        UserResponse response1 = UserResponse.builder()
                .id(1L).firstName("John").lastName("Doe").email("john@example.com")
                .location(Location.CLUJ).status(true).roles(Set.of(Role.PARTICIPANT)).build();
        UserResponse response2 = UserResponse.builder()
                .id(2L).firstName("Jane").lastName("Smith").email("jane@example.com")
                .location(Location.TIMISOARA).status(false).roles(Set.of(Role.HR)).build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.mapUserToUserResponse(user1)).thenReturn(response1);
        when(userMapper.mapUserToUserResponse(user2)).thenReturn(response2);

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getEmail()).isEqualTo("jane@example.com");

        verify(userRepository).findAll();
        verify(userMapper).mapUserToUserResponse(user1);
        verify(userMapper).mapUserToUserResponse(user2);
    }

    @Test
    void getAllUsers_callsMapperOncePerUser() {
        User user = new User(1L, "Alice", "Wonder", "alice@example.com", "pass", Location.MURES, true, Set.of(Role.ADMIN));
        UserResponse response = UserResponse.builder().id(1L).build();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.mapUserToUserResponse(user)).thenReturn(response);

        userService.getAllUsers();

        verify(userMapper, times(1)).mapUserToUserResponse(user);
    }
}

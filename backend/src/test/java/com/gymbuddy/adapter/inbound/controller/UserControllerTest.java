package com.gymbuddy.adapter.inbound.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.gymbuddy.adapter.inbound.controller.dto.UserDTO;
import com.gymbuddy.adapter.inbound.controller.mapper.UserMapper;
import com.gymbuddy.application.service.UserService;
import com.gymbuddy.domain.entity.User;
import com.gymbuddy.domain.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.gymbuddy.domain.value.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserController.
 * Tests the REST API endpoints for user management.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  private UserService userService;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserController userController;

  private User user1;
  private User user2;
  private UserDTO userDTO1;
  private UserDTO userDTO2;

  @BeforeEach
  void setUp() {
    LocalDateTime now = LocalDateTime.now();

    // Setup domain users
    user1 = User.builder()
        .id(1L)
        .username("johndoe")
        .email("john@example.com")
        .createdAt(now)
        .updatedAt(now)
        .build();

    user2 = User.builder()
        .id(2L)
        .username("janedoe")
        .email("jane@example.com")
        .createdAt(now)
        .updatedAt(now)
        .build();

    // Setup DTOs
    userDTO1 = UserDTO.builder()
        .id(1L)
        .username("johndoe")
        .email("john@example.com")
        .createdAt(now)
        .updatedAt(now)
        .build();

    userDTO2 = UserDTO.builder()
        .id(2L)
        .username("janedoe")
        .email("jane@example.com")
        .createdAt(now)
        .updatedAt(now)
        .build();
  }

  @Test
  void shouldGetAllUsers() {
    // Given
    List<User> users = Arrays.asList(user1, user2);
    when(userService.getAllUsers()).thenReturn(users);
    when(userMapper.toDto(user1)).thenReturn(userDTO1);
    when(userMapper.toDto(user2)).thenReturn(userDTO2);

    // When
    ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals("johndoe", response.getBody().get(0).getUsername());
    assertEquals("janedoe", response.getBody().get(1).getUsername());
  }

  @Test
  void shouldGetUserById() {
    // Given
    when(userService.getUserById(UserId.of(1L))).thenReturn(user1);
    when(userMapper.toDto(user1)).thenReturn(userDTO1);

    // When
    ResponseEntity<UserDTO> response = userController.getUserById(1L);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getId());
    assertEquals("johndoe", response.getBody().getUsername());
    assertEquals("john@example.com", response.getBody().getEmail());
  }

  @Test
  void shouldReturn404WhenUserNotFound() {
    // Given
    when(userService.getUserById(UserId.of(999L)))
        .thenThrow(new ResourceNotFoundException("User not found with ID: 999"));

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> {
      userController.getUserById(999L);
    });
  }

  @Test
  void shouldCreateUser() {
    // Given
    UserDTO createDTO = UserDTO.builder()
        .username("newuser")
        .email("newuser@example.com")
        .build();

    User createUser = User.builder()
        .username("newuser")
        .email("newuser@example.com")
        .build();

    User createdUser = User.builder()
        .id(3L)
        .username("newuser")
        .email("newuser@example.com")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    UserDTO createdDTO = UserDTO.builder()
        .id(3L)
        .username("newuser")
        .email("newuser@example.com")
        .createdAt(createdUser.getCreatedAt())
        .updatedAt(createdUser.getUpdatedAt())
        .build();

    when(userMapper.toDomain(any(UserDTO.class))).thenReturn(createUser);
    when(userService.createUser(any(User.class))).thenReturn(createdUser);
    when(userMapper.toDto(createdUser)).thenReturn(createdDTO);

    // When
    ResponseEntity<UserDTO> response = userController.createUser(createDTO);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(3L, response.getBody().getId());
    assertEquals("newuser", response.getBody().getUsername());
    assertEquals("newuser@example.com", response.getBody().getEmail());
  }

  @Test
  void shouldUpdateUser() {
    // Given
    UserDTO updateDTO = UserDTO.builder()
        .username("updateduser")
        .email("updated@example.com")
        .build();

    User updateUser = User.builder()
        .username("updateduser")
        .email("updated@example.com")
        .build();

    User updatedUser = User.builder()
        .id(1L)
        .username("updateduser")
        .email("updated@example.com")
        .createdAt(user1.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .build();

    UserDTO updatedDTO = UserDTO.builder()
        .id(1L)
        .username("updateduser")
        .email("updated@example.com")
        .createdAt(user1.getCreatedAt())
        .updatedAt(updatedUser.getUpdatedAt())
        .build();

    when(userMapper.toDomain(any(UserDTO.class))).thenReturn(updateUser);
    when(userService.updateUser(any(UserId.class), any(User.class))).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(updatedDTO);

    // When
    ResponseEntity<UserDTO> response = userController.updateUser(1L, updateDTO);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getId());
    assertEquals("updateduser", response.getBody().getUsername());
    assertEquals("updated@example.com", response.getBody().getEmail());
  }

  @Test
  void shouldDeleteUser() {
    // Given
    doNothing().when(userService).deleteUser(1L);

    // When
    ResponseEntity<Void> response = userController.deleteUser(1L);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentUser() {
    // Given
    when(userService.getUserById(UserId.of(999L)))
        .thenThrow(new ResourceNotFoundException("User not found with ID: 999"));

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> {
      userService.getUserById(UserId.of(999L));
    });
  }
}


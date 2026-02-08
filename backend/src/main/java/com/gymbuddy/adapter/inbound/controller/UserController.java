package com.gymbuddy.adapter.inbound.controller;

import com.gymbuddy.adapter.inbound.controller.dto.UserDTO;
import com.gymbuddy.adapter.inbound.controller.mapper.UserMapper;
import com.gymbuddy.application.service.UserService;
import com.gymbuddy.domain.entity.User;
import java.util.List;
import java.util.stream.Collectors;

import com.gymbuddy.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management.
 * Handles HTTP requests for user CRUD operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  /**
   * Retrieves all users.
   *
   * @return list of all users
   */
  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    final var users = userService.getAllUsers();
    final var userDTOs = users.stream()
        .map(userMapper::toDto)
        .toList();
    return ResponseEntity.ok(userDTOs);
  }

  /**
   * Retrieves a user by ID.
   *
   * @param id the user ID
   * @return the user
   */
  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable final Long id) {
    final var user = userService.getUserById(UserId.of(id));
    final var userDTO = userMapper.toDto(user);
    return ResponseEntity.ok(userDTO);
  }

  /**
   * Creates a new user.
   *
   * @param userDTO the user data
   * @return the created user
   */
  @PostMapping
  public ResponseEntity<UserDTO> createUser(@RequestBody final UserDTO userDTO) {
    final var user = userMapper.toDomain(userDTO);
    final var createdUser = userService.createUser(user);
    final var createdDTO = userMapper.toDto(createdUser);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
  }

  /**
   * Updates an existing user.
   *
   * @param id the user ID
   * @param userDTO the updated user data
   * @return the updated user
   */
  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> updateUser(@PathVariable final Long id, @RequestBody final UserDTO userDTO) {
    final var user = userMapper.toDomain(userDTO);
    final var updatedUser = userService.updateUser(UserId.of(id), user);
    final var updatedDTO = userMapper.toDto(updatedUser);
    return ResponseEntity.ok(updatedDTO);
  }

  /**
   * Deletes a user by ID.
   *
   * @param id the user ID
   * @return no content response
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable final Long id) {
    userService.deleteUser(UserId.of(id));
    return ResponseEntity.noContent().build();
  }
}


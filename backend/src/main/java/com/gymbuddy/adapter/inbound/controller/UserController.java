package com.gymbuddy.adapter.inbound.controller;

import com.gymbuddy.adapter.inbound.dto.UserRequestDto;
import com.gymbuddy.adapter.inbound.dto.UserResponseDto;
import com.gymbuddy.application.service.UserService;
import com.gymbuddy.domain.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API controller for user endpoints.
 * This is an inbound adapter handling HTTP requests.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * Creates a new user.
   *
   * @param userDTO the user data
   * @return the created user with 201 status
   */
  @PostMapping
  public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userDTO) {
    User createdUser = userService.createUser(toDomain(userDTO));
    return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.fromDomain(createdUser));
  }

  /**
   * Retrieves a user by ID.
   *
   * @param id the user ID
   * @return the user data
   */
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(UserResponseDto.fromDomain(userService.getUserById(id)));
  }

  /**
   * Retrieves all users.
   *
   * @return list of all users
   */
  @GetMapping
  public ResponseEntity<List<UserResponseDto>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers().stream().map(UserResponseDto::fromDomain).toList());
  }

  /**
   * Updates an existing user.
   *
   * @param id the user ID
   * @param userDTO the updated user data
   * @return the updated user
   */
  @PutMapping("/{id}")
  public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto userDTO) {
    User updated = userService.updateUser(id, toDomain(userDTO));
    return ResponseEntity.ok(UserResponseDto.fromDomain(updated));
  }

  private User toDomain(UserRequestDto dto) {
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    return user;
  }

  /**
   * Deletes a user by ID.
   *
   * @param id the user ID
   * @return no content response
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}

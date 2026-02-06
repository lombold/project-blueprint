package com.gymbuddy.adapter.inbound.controller;

import com.gymbuddy.application.dto.UserDTO;
import com.gymbuddy.application.service.UserService;
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
  public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
    UserDTO created = userService.createUser(userDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  /**
   * Retrieves a user by ID.
   *
   * @param id the user ID
   * @return the user data
   */
  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  /**
   * Retrieves all users.
   *
   * @return list of all users
   */
  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  /**
   * Updates an existing user.
   *
   * @param id the user ID
   * @param userDTO the updated user data
   * @return the updated user
   */
  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
    return ResponseEntity.ok(userService.updateUser(id, userDTO));
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

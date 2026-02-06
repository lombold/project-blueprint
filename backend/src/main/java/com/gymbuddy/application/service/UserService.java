package com.gymbuddy.application.service;

import com.gymbuddy.application.dto.UserDTO;
import com.gymbuddy.application.port.UserPort;
import com.gymbuddy.domain.entity.User;
import com.gymbuddy.domain.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use-case service for user operations.
 * Orchestrates user domain logic and persistence operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserPort userPort;

  /**
   * Creates a new user.
   *
   * @param userDTO the user data transfer object
   * @return the created user DTO
   */
  public UserDTO createUser(UserDTO userDTO) {
    User user = new User();
    user.setUsername(userDTO.getUsername());
    user.setEmail(userDTO.getEmail());
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    user.validate();

    User savedUser = userPort.save(user);
    return mapToDTO(savedUser);
  }

  /**
   * Retrieves a user by ID.
   *
   * @param id the user ID
   * @return the user DTO
   * @throws ResourceNotFoundException if user is not found
   */
  public UserDTO getUserById(Long id) {
    User user =
        userPort
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    return mapToDTO(user);
  }

  /**
   * Retrieves all users.
   *
   * @return a list of user DTOs
   */
  public List<UserDTO> getAllUsers() {
    return userPort.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
  }

  /**
   * Updates an existing user.
   *
   * @param id the user ID
   * @param userDTO the updated user data
   * @return the updated user DTO
   * @throws ResourceNotFoundException if user is not found
   */
  public UserDTO updateUser(Long id, UserDTO userDTO) {
    User user =
        userPort
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

    user.setUsername(userDTO.getUsername());
    user.setEmail(userDTO.getEmail());
    user.setUpdatedAt(LocalDateTime.now());
    user.validate();

    User updatedUser = userPort.save(user);
    return mapToDTO(updatedUser);
  }

  /**
   * Deletes a user by ID.
   *
   * @param id the user ID
   * @throws ResourceNotFoundException if user is not found
   */
  public void deleteUser(Long id) {
    if (userPort.findById(id).isEmpty()) {
      throw new ResourceNotFoundException("User not found with ID: " + id);
    }
    userPort.deleteById(id);
  }

  private UserDTO mapToDTO(User user) {
    return UserDTO.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }
}

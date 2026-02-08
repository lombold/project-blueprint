package com.gymbuddy.adapter.inbound.controller;

import com.gymbuddy.adapter.inbound.controller.dto.UserDTO;
import com.gymbuddy.adapter.inbound.controller.mapper.UserMapper;
import com.gymbuddy.application.service.UserService;
import com.gymbuddy.domain.value.UserId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user management.
 * Handles HTTP requests for user CRUD operations.
 */
@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

  private final UserService userService;
  private final UserMapper userMapper;

  @Override
  public ResponseEntity<List<UserDTO>> listUsers() {
    final var users = userService.getAllUsers();
    final var userDTOs = users.stream()
        .map(userMapper::toDto)
        .toList();
    return ResponseEntity.ok(userDTOs);
  }

  @Override
  public ResponseEntity<UserDTO> getUserById(final Long id) {
    final var user = userService.getUserById(UserId.of(id));
    final var userDTO = userMapper.toDto(user);
    return ResponseEntity.ok(userDTO);
  }

  @Override
  public ResponseEntity<UserDTO> createUser(final UserDTO userDTO) {
    final var user = userMapper.toDomain(userDTO);
    final var createdUser = userService.createUser(user);
    final var createdDTO = userMapper.toDto(createdUser);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
  }

  @Override
  public ResponseEntity<UserDTO> updateUser(final Long id, final UserDTO userDTO) {
    final var user = userMapper.toDomain(userDTO);
    final var updatedUser = userService.updateUser(UserId.of(id), user);
    final var updatedDTO = userMapper.toDto(updatedUser);
    return ResponseEntity.ok(updatedDTO);
  }

  @Override
  public ResponseEntity<Void> deleteUser(final Long id) {
    userService.deleteUser(UserId.of(id));
    return ResponseEntity.noContent().build();
  }
}

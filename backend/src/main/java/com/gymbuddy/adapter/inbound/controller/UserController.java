package com.gymbuddy.adapter.inbound.controller;

import com.gymbuddy.adapter.inbound.controller.dto.UserDto;
import com.gymbuddy.adapter.inbound.controller.mapper.UserMapper;
import com.gymbuddy.application.port.in.UserUseCase;
import com.gymbuddy.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for user management.
 * Handles HTTP requests for user CRUD operations.
 */
@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<List<UserDto>> listUsers() {
        final var users = userUseCase.getAllUsers();
        final var userDTOs = users.stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(userDTOs);
    }

    @Override
    public ResponseEntity<UserDto> getUserById(final Long id) {
        final var user = userUseCase.getUserById(UserId.of(id));
        final var userDTO = userMapper.toDto(user);
        return ResponseEntity.ok(userDTO);
    }

    @Override
    public ResponseEntity<UserDto> createUser(final UserDto userDTO) {
        final var user = userMapper.toDomain(userDTO);
        final var createdUser = userUseCase.createUser(user);
        final var createdDTO = userMapper.toDto(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
    }

    @Override
    public ResponseEntity<UserDto> updateUser(final Long id, final UserDto userDTO) {
        final var user = userMapper.toDomain(userDTO);
        final var updatedUser = userUseCase.updateUser(UserId.of(id), user);
        final var updatedDTO = userMapper.toDto(updatedUser);
        return ResponseEntity.ok(updatedDTO);
    }

    @Override
    public ResponseEntity<Void> deleteUser(final Long id) {
        userUseCase.deleteUser(UserId.of(id));
        return ResponseEntity.noContent().build();
    }
}

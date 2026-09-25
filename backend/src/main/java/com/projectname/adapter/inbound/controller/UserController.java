package com.projectname.adapter.inbound.controller;

import com.projectname.adapter.inbound.controller.dto.UserDto;
import com.projectname.adapter.inbound.controller.mapper.UserMapper;
import com.projectname.application.port.in.CreateUserCommand;
import com.projectname.application.port.in.DeleteUserCommand;
import com.projectname.application.port.in.GetUserByIdQuery;
import com.projectname.application.port.in.ListUsersQuery;
import com.projectname.application.port.in.UpdateUserCommand;
import com.projectname.domain.value.UserId;
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

    private final CreateUserCommand createUserCommand;
    private final UpdateUserCommand updateUserCommand;
    private final DeleteUserCommand deleteUserCommand;
    private final GetUserByIdQuery getUserByIdQuery;
    private final ListUsersQuery listUsersQuery;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<List<UserDto>> listUsers() {
        final var users = listUsersQuery.invoke();
        final var userDTOs = users.stream().map(userMapper::toDto).toList();
        return ResponseEntity.ok(userDTOs);
    }

    @Override
    public ResponseEntity<UserDto> getUserById(final Long id) {
        final var user = getUserByIdQuery.invoke(UserId.of(id));
        final var userDTO = userMapper.toDto(user);
        return ResponseEntity.ok(userDTO);
    }

    @Override
    public ResponseEntity<UserDto> createUser(final UserDto userDTO) {
        final var user = userMapper.toDomain(userDTO);
        final var createdUser = createUserCommand.invoke(user);
        final var createdDTO = userMapper.toDto(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
    }

    @Override
    public ResponseEntity<UserDto> updateUser(final Long id, final UserDto userDTO) {
        final var user = userMapper.toDomain(userDTO);
        final var updatedUser = updateUserCommand.invoke(UserId.of(id), user);
        final var updatedDTO = userMapper.toDto(updatedUser);
        return ResponseEntity.ok(updatedDTO);
    }

    @Override
    public ResponseEntity<Void> deleteUser(final Long id) {
        deleteUserCommand.invoke(UserId.of(id));
        return ResponseEntity.noContent().build();
    }
}

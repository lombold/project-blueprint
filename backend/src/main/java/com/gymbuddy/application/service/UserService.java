package com.gymbuddy.application.service;

import com.gymbuddy.application.port.in.UserUseCase;
import com.gymbuddy.application.port.UserPort;
import com.gymbuddy.domain.entity.User;
import com.gymbuddy.domain.exception.ResourceNotFoundException;
import java.time.OffsetDateTime;
import java.util.List;

import com.gymbuddy.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

  private final UserPort userPort;

  @Override
  public User createUser(User user) {
    OffsetDateTime now = OffsetDateTime.now();
    user.setCreatedAt(now);
    user.setUpdatedAt(now);
    user.validate();
    return userPort.save(user);
  }

  @Override
  public User getUserById(UserId id) {
    return userPort
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
  }

  @Override
  public List<User> getAllUsers() {
    return userPort.findAll();
  }

  @Override
  public User updateUser(UserId id, User userUpdates) {
    User existingUser = getUserById(id);
    if (userUpdates.getUsername() != null) {
      existingUser.setUsername(userUpdates.getUsername());
    }
    if (userUpdates.getEmail() != null) {
      existingUser.setEmail(userUpdates.getEmail());
    }
    existingUser.setUpdatedAt(OffsetDateTime.now());
    existingUser.validate();
    return userPort.save(existingUser);
  }

  @Override
  public void deleteUser(UserId id) {
    if (userPort.findById(id).isEmpty()) {
      throw new ResourceNotFoundException("User not found with ID: " + id);
    }
    userPort.deleteById(id);
  }
}

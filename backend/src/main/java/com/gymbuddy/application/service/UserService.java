package com.gymbuddy.application.service;

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
public class UserService {

  private final UserPort userPort;

  public User createUser(User user) {
    OffsetDateTime now = OffsetDateTime.now();
    user.setCreatedAt(now);
    user.setUpdatedAt(now);
    user.validate();
    return userPort.save(user);
  }

  public User getUserById(UserId id) {
    return userPort
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
  }

  public List<User> getAllUsers() {
    return userPort.findAll();
  }

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

  public void deleteUser(UserId id) {
    if (userPort.findById(id).isEmpty()) {
      throw new ResourceNotFoundException("User not found with ID: " + id);
    }
    userPort.deleteById(id);
  }
}

package com.gymbuddy.application.port.in;

import com.gymbuddy.domain.entity.User;
import com.gymbuddy.domain.value.UserId;
import java.util.List;

public interface UserUseCase {

  User createUser(User user);

  User getUserById(UserId id);

  List<User> getAllUsers();

  User updateUser(UserId id, User userUpdates);

  void deleteUser(UserId id);
}

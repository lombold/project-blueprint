package com.projectname.application.port.in;

import com.projectname.domain.entity.User;
import com.projectname.domain.value.UserId;
import java.util.List;

public interface UserUseCase {

  User createUser(User user);

  User getUserById(UserId id);

  List<User> getAllUsers();

  User updateUser(UserId id, User userUpdates);

  void deleteUser(UserId id);
}

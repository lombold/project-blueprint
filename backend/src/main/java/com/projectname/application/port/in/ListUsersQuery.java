package com.projectname.application.port.in;

import com.projectname.domain.entity.User;
import java.util.List;

/** Lists all users. */
public interface ListUsersQuery {
    List<User> invoke();
}

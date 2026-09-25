package com.projectname.application.port.in;

import com.projectname.domain.entity.User;

/** Creates a user. */
public interface CreateUserCommand {
    User invoke(User user);
}

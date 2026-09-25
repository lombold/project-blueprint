package com.projectname.application.port.in;

import com.projectname.domain.entity.User;
import com.projectname.domain.value.UserId;

/** Updates a user. */
public interface UpdateUserCommand {
    User invoke(UserId id, User updates);
}

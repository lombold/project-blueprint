package com.projectname.application.port.in;

import com.projectname.domain.entity.User;
import com.projectname.domain.value.UserId;

/** Retrieves a user by ID. */
public interface GetUserByIdQuery {
    User invoke(UserId id);
}

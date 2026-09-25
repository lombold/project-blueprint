package com.projectname.application.port.in;

import com.projectname.domain.value.UserId;

/** Deletes a user. */
public interface DeleteUserCommand {
    void invoke(UserId id);
}

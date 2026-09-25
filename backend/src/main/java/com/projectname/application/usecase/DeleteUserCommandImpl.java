package com.projectname.application.usecase;

import com.projectname.application.port.in.DeleteUserCommand;
import com.projectname.application.port.out.UserPort;
import com.projectname.domain.exception.ResourceNotFoundException;
import com.projectname.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Deletes a user after checking that it exists.
 */
@Service
@RequiredArgsConstructor
public class DeleteUserCommandImpl implements DeleteUserCommand {
    private final UserPort userPort;

    @Override
    public void invoke(final UserId id) {
        if (userPort.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userPort.deleteById(id);
    }
}

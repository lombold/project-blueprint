package com.projectname.application.usecase;

import com.projectname.application.port.in.UpdateUserCommand;
import com.projectname.application.port.out.UserPort;
import com.projectname.domain.entity.User;
import com.projectname.domain.exception.ResourceNotFoundException;
import com.projectname.domain.value.UserId;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Applies permitted changes to an existing user. */
@Service
@RequiredArgsConstructor
public class UpdateUserCommandImpl implements UpdateUserCommand {
    private final UserPort userPort;

    @Override
    public User invoke(final UserId id, final User updates) {
        final var existingUser =
                userPort.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        if (updates.getUsername() != null) {
            existingUser.setUsername(updates.getUsername());
        }
        if (updates.getEmail() != null) {
            existingUser.setEmail(updates.getEmail());
        }
        existingUser.setUpdatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
        existingUser.validate();
        return userPort.save(existingUser);
    }
}

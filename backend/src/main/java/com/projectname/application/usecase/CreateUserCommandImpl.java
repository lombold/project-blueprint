package com.projectname.application.usecase;

import com.projectname.application.port.in.CreateUserCommand;
import com.projectname.application.port.out.UserPort;
import com.projectname.domain.entity.User;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Validates and persists a new user. */
@Service
@RequiredArgsConstructor
public class CreateUserCommandImpl implements CreateUserCommand {
    private final UserPort userPort;

    @Override
    public User invoke(final User user) {
        user.setId(null); // prevent client-supplied id from overwriting an existing row via save()
        final var now = OffsetDateTime.now(ZoneId.systemDefault());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.validate();
        return userPort.save(user);
    }
}

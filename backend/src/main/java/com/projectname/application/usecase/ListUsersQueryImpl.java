package com.projectname.application.usecase;

import com.projectname.application.port.in.ListUsersQuery;
import com.projectname.application.port.out.UserPort;
import com.projectname.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Loads all users.
 */
@Service
@RequiredArgsConstructor
public class ListUsersQueryImpl implements ListUsersQuery {
    private final UserPort userPort;

    @Override
    public List<User> invoke() {
        return userPort.findAll();
    }
}

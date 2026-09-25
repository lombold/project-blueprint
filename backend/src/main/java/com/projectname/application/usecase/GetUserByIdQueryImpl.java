package com.projectname.application.usecase;

import com.projectname.application.port.UserPort;
import com.projectname.application.port.in.GetUserByIdQuery;
import com.projectname.domain.entity.User;
import com.projectname.domain.exception.ResourceNotFoundException;
import com.projectname.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Loads a user or reports its absence. */
@Service
@RequiredArgsConstructor
public class GetUserByIdQueryImpl implements GetUserByIdQuery {
    private final UserPort userPort;

    @Override
    public User invoke(final UserId id) {
        return userPort.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}

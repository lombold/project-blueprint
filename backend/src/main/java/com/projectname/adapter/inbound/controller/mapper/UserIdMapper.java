package com.projectname.adapter.inbound.controller.mapper;

import com.projectname.domain.value.UserId;
import org.mapstruct.Mapper;

/** Converts HTTP user identifiers to and from domain value objects. */
@Mapper
public interface UserIdMapper {

    default UserId toUserId(final Long value) {
        if (value == null) {
            return null;
        }
        return UserId.of(value);
    }

    default Long toLong(final UserId userId) {
        if (userId == null) {
            return null;
        }
        return userId.toLong();
    }
}

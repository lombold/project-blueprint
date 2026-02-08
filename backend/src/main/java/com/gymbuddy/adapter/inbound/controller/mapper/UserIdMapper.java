package com.gymbuddy.adapter.inbound.controller.mapper;

import com.gymbuddy.domain.value.UserId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
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


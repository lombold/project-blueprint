package com.gymbuddy.adapter.outbound.persistence.mapper;

import com.gymbuddy.domain.value.UserId;
import org.mapstruct.Mapper;

@Mapper
public interface UserJpaIdMapper {

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

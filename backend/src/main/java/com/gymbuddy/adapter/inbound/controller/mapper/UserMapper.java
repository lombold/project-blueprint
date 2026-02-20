package com.gymbuddy.adapter.inbound.controller.mapper;

import com.gymbuddy.adapter.inbound.controller.dto.UserDto;
import com.gymbuddy.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(uses = {UserIdMapper.class})
public interface UserMapper {

  UserDto toDto(User user);

  User toDomain(UserDto UserDto);
}


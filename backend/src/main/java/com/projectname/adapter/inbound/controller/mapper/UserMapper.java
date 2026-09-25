package com.projectname.adapter.inbound.controller.mapper;

import com.projectname.adapter.inbound.controller.dto.UserDto;
import com.projectname.domain.entity.User;
import org.mapstruct.Mapper;

/** Maps users between the domain model and HTTP DTOs. */
@Mapper(uses = {UserIdMapper.class})
public interface UserMapper {

    UserDto toDto(User user);

    User toDomain(UserDto userDto);
}

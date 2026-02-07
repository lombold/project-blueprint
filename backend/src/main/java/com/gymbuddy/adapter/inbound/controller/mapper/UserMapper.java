package com.gymbuddy.adapter.inbound.controller.mapper;

import com.gymbuddy.adapter.inbound.controller.dto.UserDTO;
import com.gymbuddy.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = WorkoutMapper.class)
public interface UserMapper {

  UserDTO toDto(User user);

  User toDomain(UserDTO userDTO);
}


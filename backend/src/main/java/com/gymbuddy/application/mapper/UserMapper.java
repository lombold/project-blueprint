package com.gymbuddy.application.mapper;

import com.gymbuddy.application.dto.UserDTO;
import com.gymbuddy.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = WorkoutMapper.class)
public interface UserMapper {

  UserDTO toDto(User user);

  User toDomain(UserDTO userDTO);

  void updateUserFromDto(UserDTO userDTO, @MappingTarget User user);
}

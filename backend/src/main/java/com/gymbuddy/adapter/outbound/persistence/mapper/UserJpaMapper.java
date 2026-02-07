package com.gymbuddy.adapter.outbound.persistence.mapper;

import com.gymbuddy.adapter.outbound.persistence.UserJpaEntity;
import com.gymbuddy.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = WorkoutJpaMapper.class)
public interface UserJpaMapper {

  UserJpaEntity toJpaEntity(User user);

  User toDomain(UserJpaEntity userJpaEntity);
}

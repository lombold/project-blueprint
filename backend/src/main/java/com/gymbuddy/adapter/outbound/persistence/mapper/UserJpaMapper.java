package com.gymbuddy.adapter.outbound.persistence.mapper;

import com.gymbuddy.adapter.inbound.controller.mapper.UserIdMapper;
import com.gymbuddy.adapter.outbound.persistence.UserJpaEntity;
import com.gymbuddy.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { UserIdMapper.class})
public interface UserJpaMapper {

  UserJpaEntity toJpaEntity(User user);

  User toDomain(UserJpaEntity userJpaEntity);
}

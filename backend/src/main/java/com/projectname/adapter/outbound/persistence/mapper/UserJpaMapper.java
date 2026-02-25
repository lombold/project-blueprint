package com.projectname.adapter.outbound.persistence.mapper;

import com.projectname.adapter.outbound.persistence.UserJpaEntity;
import com.projectname.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(uses = {UserJpaIdMapper.class})
public interface UserJpaMapper {

  UserJpaEntity toJpaEntity(User user);

  User toDomain(UserJpaEntity userJpaEntity);
}

package com.gymbuddy.adapter.outbound.persistence.mapper;

import com.gymbuddy.adapter.outbound.persistence.ExerciseJpaEntity;
import com.gymbuddy.domain.entity.Exercise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseJpaMapper {

  ExerciseJpaEntity toJpaEntity(Exercise exercise);

  Exercise toDomain(ExerciseJpaEntity exerciseJpaEntity);
}

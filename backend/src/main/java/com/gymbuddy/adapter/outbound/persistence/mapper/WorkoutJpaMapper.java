package com.gymbuddy.adapter.outbound.persistence.mapper;

import com.gymbuddy.adapter.outbound.persistence.WorkoutJpaEntity;
import com.gymbuddy.domain.entity.Workout;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ExerciseJpaMapper.class)
public interface WorkoutJpaMapper {

  WorkoutJpaEntity toJpaEntity(Workout workout);

  Workout toDomain(WorkoutJpaEntity workoutJpaEntity);
}

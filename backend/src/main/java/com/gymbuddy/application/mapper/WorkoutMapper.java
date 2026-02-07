package com.gymbuddy.application.mapper;

import com.gymbuddy.application.dto.WorkoutDTO;
import com.gymbuddy.domain.entity.Workout;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ExerciseMapper.class)
public interface WorkoutMapper {

  WorkoutDTO toDto(Workout workout);

  Workout toDomain(WorkoutDTO workoutDTO);
}

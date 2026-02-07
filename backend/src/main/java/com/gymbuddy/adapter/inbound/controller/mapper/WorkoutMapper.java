package com.gymbuddy.adapter.inbound.controller.mapper;

import com.gymbuddy.adapter.inbound.controller.dto.WorkoutDTO;
import com.gymbuddy.domain.entity.Workout;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ExerciseMapper.class)
public interface WorkoutMapper {

  WorkoutDTO toDto(Workout workout);

  Workout toDomain(WorkoutDTO workoutDTO);
}

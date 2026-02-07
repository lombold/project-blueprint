package com.gymbuddy.application.mapper;

import com.gymbuddy.application.dto.ExerciseDTO;
import com.gymbuddy.domain.entity.Exercise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

  ExerciseDTO toDto(Exercise exercise);

  Exercise toDomain(ExerciseDTO exerciseDTO);
}

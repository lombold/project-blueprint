package com.gymbuddy.adapter.inbound.controller.mapper;

import com.gymbuddy.adapter.inbound.controller.dto.ExerciseDTO;
import com.gymbuddy.domain.entity.Exercise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

  ExerciseDTO toDto(Exercise exercise);

  Exercise toDomain(ExerciseDTO exerciseDTO);
}

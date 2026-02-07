package com.gymbuddy.adapter.inbound.mapper;

import com.gymbuddy.adapter.inbound.dto.UserRequestDto;
import com.gymbuddy.adapter.inbound.dto.UserResponseDto;
import com.gymbuddy.domain.entity.User;
import com.gymbuddy.domain.entity.Workout;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserControllerMapper {

  public User toDomain(UserRequestDto dto) {
    if (dto == null) {
      return null;
    }

    return User.builder().username(dto.getUsername()).email(dto.getEmail()).build();
  }

  public UserResponseDto toResponse(User user) {
    if (user == null) {
      return null;
    }

    UserResponseDto response = new UserResponseDto();
    response.setId(user.getId());
    response.setUsername(user.getUsername());
    response.setEmail(user.getEmail());
    response.setCreatedAt(user.getCreatedAt());
    response.setUpdatedAt(user.getUpdatedAt());
    response.setWorkouts(mapWorkouts(user.getWorkouts()));
x
    return response;
  }

  public List<UserResponseDto> toResponseList(List<User> users) {
    if (users == null) {
      return Collections.emptyList();
    }
    return users.stream().map(this::toResponse).collect(Collectors.toList());
  }

  private List<UserResponseDto.WorkoutSummaryDto> mapWorkouts(List<Workout> workouts) {
    return Objects.requireNonNullElse(workouts, List.<Workout>of()).stream()
        .map(
            workout -> {
              UserResponseDto.WorkoutSummaryDto summary = new UserResponseDto.WorkoutSummaryDto();
              summary.setId(workout.getId());
              summary.setName(workout.getName());
              summary.setExerciseCount(workout.getExerciseCount());
              return summary;
            })
        .collect(Collectors.toList());
  }
}

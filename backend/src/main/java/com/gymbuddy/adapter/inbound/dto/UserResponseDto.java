package com.gymbuddy.adapter.inbound.dto;

import com.gymbuddy.domain.entity.User;
import com.gymbuddy.domain.entity.Workout;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * HTTP response payload representing a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

  private Long id;
  private String username;
  private String email;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private List<WorkoutSummaryDto> workouts = new ArrayList<>();

  public static UserResponseDto fromDomain(User user) {
    UserResponseDto dto = new UserResponseDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setCreatedAt(user.getCreatedAt());
    dto.setUpdatedAt(user.getUpdatedAt());
    dto.setWorkouts(
        Objects.requireNonNullElse(user.getWorkouts(), List.<Workout>of()).stream()
            .map(
                workout -> {
                  WorkoutSummaryDto summary = new WorkoutSummaryDto();
                  summary.setId(workout.getId());
                  summary.setName(workout.getName());
                  summary.setExerciseCount(workout.getExerciseCount());
                  return summary;
                })
            .toList());
    return dto;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WorkoutSummaryDto {
    private Long id;
    private String name;
    private Integer exerciseCount;
  }
}

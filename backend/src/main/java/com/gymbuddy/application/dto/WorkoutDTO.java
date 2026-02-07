package com.gymbuddy.application.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for Workout.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutDTO {

  private Long id;
  private Long userId;
  private String name;
  private String description;
  private Integer durationMinutes;
  private Integer exerciseCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  @Builder.Default
  private List<ExerciseDTO> exercises = new ArrayList<>();
}

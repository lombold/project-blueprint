package com.gymbuddy.domain.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Exercise domain entity.
 * Represents a single exercise within a workout.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

  private Long id;
  private Long workoutId;
  private String name;
  private Integer sets;
  private Integer reps;
  private Double weightKg;
  private String notes;
  private Integer orderIndex;
  private LocalDateTime createdAt;

  /**
   * Validates that the exercise has valid data.
   *
   * @throws IllegalArgumentException if name or workout ID is invalid
   */
  public void validate() {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Exercise name cannot be empty");
    }
    if (workoutId == null || workoutId <= 0) {
      throw new IllegalArgumentException("Workout ID must be valid");
    }
  }
}

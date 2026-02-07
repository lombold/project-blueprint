package com.gymbuddy.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Workout domain entity.
 * Represents a workout session in the gym-buddy application.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workout {

  private Long id;
  private Long userId;
  private User user;
  private String name;
  private String description;
  private Integer durationMinutes;
  private Integer exerciseCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Builder.Default
  private List<Exercise> exercises = new ArrayList<>();

  /**
   * Validates that the workout has valid data.
   *
   * @throws IllegalArgumentException if name is invalid
   */
  public void validate() {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Workout name cannot be empty");
    }
    if (userId == null || userId <= 0) {
      throw new IllegalArgumentException("User ID must be valid");
    }
  }

  /**
   * Adds an exercise to this workout.
   *
   * @param exercise the exercise to add
   */
  public void addExercise(Exercise exercise) {
    if (!exercises.contains(exercise)) {
      exercises.add(exercise);
      this.exerciseCount = exercises.size();
    }
  }

  /**
   * Removes an exercise from this workout.
   *
   * @param exercise the exercise to remove
   */
  public void removeExercise(Exercise exercise) {
    if (exercises.remove(exercise)) {
      this.exerciseCount = exercises.size();
    }
  }
}

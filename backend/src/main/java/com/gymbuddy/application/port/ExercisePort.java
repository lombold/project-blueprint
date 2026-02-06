package com.gymbuddy.application.port;

import com.gymbuddy.domain.entity.Exercise;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port for exercise persistence operations.
 * This interface defines the contract for exercise repository implementations.
 */
public interface ExercisePort {

  /**
   * Saves an exercise (create or update).
   *
   * @param exercise the exercise to save
   * @return the saved exercise with ID populated
   */
  Exercise save(Exercise exercise);

  /**
   * Retrieves an exercise by ID.
   *
   * @param id the exercise ID
   * @return an Optional containing the exercise if found
   */
  Optional<Exercise> findById(Long id);

  /**
   * Retrieves all exercises for a specific workout.
   *
   * @param workoutId the workout ID
   * @return a list of exercises for the workout
   */
  List<Exercise> findByWorkoutId(Long workoutId);

  /**
   * Deletes an exercise by ID.
   *
   * @param id the exercise ID
   */
  void deleteById(Long id);
}

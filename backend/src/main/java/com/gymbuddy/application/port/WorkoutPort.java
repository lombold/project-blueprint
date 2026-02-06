package com.gymbuddy.application.port;

import com.gymbuddy.domain.entity.Workout;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port for workout persistence operations.
 * This interface defines the contract for workout repository implementations.
 */
public interface WorkoutPort {

  /**
   * Saves a workout (create or update).
   *
   * @param workout the workout to save
   * @return the saved workout with ID populated
   */
  Workout save(Workout workout);

  /**
   * Retrieves a workout by ID.
   *
   * @param id the workout ID
   * @return an Optional containing the workout if found
   */
  Optional<Workout> findById(Long id);

  /**
   * Retrieves all workouts for a specific user.
   *
   * @param userId the user ID
   * @return a list of workouts for the user
   */
  List<Workout> findByUserId(Long userId);

  /**
   * Retrieves all workouts.
   *
   * @return a list of all workouts
   */
  List<Workout> findAll();

  /**
   * Deletes a workout by ID.
   *
   * @param id the workout ID
   */
  void deleteById(Long id);
}

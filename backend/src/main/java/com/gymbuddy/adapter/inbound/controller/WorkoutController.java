package com.gymbuddy.adapter.inbound.controller;

import com.gymbuddy.adapter.inbound.controller.dto.WorkoutDTO;
import com.gymbuddy.adapter.inbound.controller.mapper.WorkoutMapper;
import com.gymbuddy.application.service.WorkoutService;
import com.gymbuddy.domain.entity.Workout;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for workout management.
 * Handles HTTP requests for workout CRUD operations.
 */
@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
public class WorkoutController {

  private final WorkoutService workoutService;
  private final WorkoutMapper workoutMapper;

  /**
   * Retrieves all workouts.
   *
   * @return list of all workouts
   */
  @GetMapping
  public ResponseEntity<List<WorkoutDTO>> getAllWorkouts() {
    final var workouts = workoutService.getAllWorkouts();
    final var workoutDTOs = workouts.stream()
        .map(workoutMapper::toDto)
        .toList();
    return ResponseEntity.ok(workoutDTOs);
  }

  /**
   * Retrieves a workout by ID.
   *
   * @param id the workout ID
   * @return the workout
   */
  @GetMapping("/{id}")
  public ResponseEntity<WorkoutDTO> getWorkoutById(@PathVariable final Long id) {
    final var workout = workoutService.getWorkoutById(id);
    final var workoutDTO = workoutMapper.toDto(workout);
    return ResponseEntity.ok(workoutDTO);
  }

  /**
   * Retrieves all workouts for a specific user.
   *
   * @param userId the user ID
   * @return list of workouts for the user
   */
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<WorkoutDTO>> getWorkoutsByUserId(@PathVariable final Long userId) {
    final var workouts = workoutService.getWorkoutsByUserId(userId);
    final var workoutDTOs = workouts.stream()
        .map(workoutMapper::toDto)
        .toList();
    return ResponseEntity.ok(workoutDTOs);
  }

  /**
   * Creates a new workout.
   *
   * @param workoutDTO the workout data
   * @return the created workout
   */
  @PostMapping
  public ResponseEntity<WorkoutDTO> createWorkout(@RequestBody final WorkoutDTO workoutDTO) {
    final var workout = workoutMapper.toDomain(workoutDTO);
    final var createdWorkout = workoutService.createWorkout(workout);
    final var createdDTO = workoutMapper.toDto(createdWorkout);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
  }

  /**
   * Updates an existing workout.
   *
   * @param id the workout ID
   * @param workoutDTO the updated workout data
   * @return the updated workout
   */
  @PutMapping("/{id}")
  public ResponseEntity<WorkoutDTO> updateWorkout(@PathVariable final Long id, @RequestBody final WorkoutDTO workoutDTO) {
    final var workout = workoutMapper.toDomain(workoutDTO);
    final var updatedWorkout = workoutService.updateWorkout(id, workout);
    final var updatedDTO = workoutMapper.toDto(updatedWorkout);
    return ResponseEntity.ok(updatedDTO);
  }

  /**
   * Deletes a workout by ID.
   *
   * @param id the workout ID
   * @return no content response
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWorkout(@PathVariable final Long id) {
    workoutService.deleteWorkout(id);
    return ResponseEntity.noContent().build();
  }
}


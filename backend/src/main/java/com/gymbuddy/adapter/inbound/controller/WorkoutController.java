package com.gymbuddy.adapter.inbound.controller;

import com.gymbuddy.adapter.inbound.controller.dto.WorkoutDTO;
import com.gymbuddy.adapter.inbound.controller.mapper.WorkoutMapper;
import com.gymbuddy.application.service.WorkoutService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for workout management.
 * Handles HTTP requests for workout CRUD operations.
 */
@RestController
@RequiredArgsConstructor
public class WorkoutController implements WorkoutsApi {

  private final WorkoutService workoutService;
  private final WorkoutMapper workoutMapper;

  @Override
  public ResponseEntity<List<WorkoutDTO>> listWorkouts() {
    final var workouts = workoutService.getAllWorkouts();
    final var workoutDTOs = workouts.stream()
        .map(workoutMapper::toDto)
        .toList();
    return ResponseEntity.ok(workoutDTOs);
  }

  @Override
  public ResponseEntity<WorkoutDTO> getWorkoutById(final Long id) {
    final var workout = workoutService.getWorkoutById(id);
    final var workoutDTO = workoutMapper.toDto(workout);
    return ResponseEntity.ok(workoutDTO);
  }

  @Override
  public ResponseEntity<List<WorkoutDTO>> listWorkoutsByUser(final Long userId) {
    final var workouts = workoutService.getWorkoutsByUserId(userId);
    final var workoutDTOs = workouts.stream()
        .map(workoutMapper::toDto)
        .toList();
    return ResponseEntity.ok(workoutDTOs);
  }

  @Override
  public ResponseEntity<WorkoutDTO> createWorkout(final WorkoutDTO workoutDTO) {
    final var workout = workoutMapper.toDomain(workoutDTO);
    final var createdWorkout = workoutService.createWorkout(workout);
    final var createdDTO = workoutMapper.toDto(createdWorkout);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
  }

  @Override
  public ResponseEntity<WorkoutDTO> updateWorkout(final Long id, final WorkoutDTO workoutDTO) {
    final var workout = workoutMapper.toDomain(workoutDTO);
    final var updatedWorkout = workoutService.updateWorkout(id, workout);
    final var updatedDTO = workoutMapper.toDto(updatedWorkout);
    return ResponseEntity.ok(updatedDTO);
  }

  @Override
  public ResponseEntity<Void> deleteWorkout(final Long id) {
    workoutService.deleteWorkout(id);
    return ResponseEntity.noContent().build();
  }
}

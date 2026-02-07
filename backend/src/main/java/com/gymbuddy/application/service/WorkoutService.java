package com.gymbuddy.application.service;

import com.gymbuddy.application.port.WorkoutPort;
import com.gymbuddy.domain.entity.Workout;
import com.gymbuddy.domain.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkoutService {

  private final WorkoutPort workoutPort;

  public Workout createWorkout(Workout workout) {
    LocalDateTime now = LocalDateTime.now();
    workout.setCreatedAt(now);
    workout.setUpdatedAt(now);
    workout.validate();
    return workoutPort.save(workout);
  }

  public Workout getWorkoutById(Long id) {
    return workoutPort
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Workout not found with ID: " + id));
  }

  public List<Workout> getAllWorkouts() {
    return workoutPort.findAll();
  }

  public List<Workout> getWorkoutsByUserId(Long userId) {
    return workoutPort.findByUserId(userId);
  }

  public Workout updateWorkout(Long id, Workout workoutUpdates) {
    Workout existingWorkout = getWorkoutById(id);
    if (workoutUpdates.getName() != null) {
      existingWorkout.setName(workoutUpdates.getName());
    }
    if (workoutUpdates.getDescription() != null) {
      existingWorkout.setDescription(workoutUpdates.getDescription());
    }
    if (workoutUpdates.getDurationMinutes() != null) {
      existingWorkout.setDurationMinutes(workoutUpdates.getDurationMinutes());
    }
    existingWorkout.setUpdatedAt(LocalDateTime.now());
    existingWorkout.validate();
    return workoutPort.save(existingWorkout);
  }

  public void deleteWorkout(Long id) {
    if (workoutPort.findById(id).isEmpty()) {
      throw new ResourceNotFoundException("Workout not found with ID: " + id);
    }
    workoutPort.deleteById(id);
  }
}


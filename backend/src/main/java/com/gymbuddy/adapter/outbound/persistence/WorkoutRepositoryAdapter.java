package com.gymbuddy.adapter.outbound.persistence;

import com.gymbuddy.adapter.outbound.persistence.mapper.WorkoutJpaMapper;
import com.gymbuddy.application.port.UserPort;
import com.gymbuddy.application.port.WorkoutPort;
import com.gymbuddy.domain.entity.Workout;
import com.gymbuddy.domain.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkoutRepositoryAdapter implements WorkoutPort {

  private final WorkoutJpaRepository workoutJpaRepository;
  private final WorkoutJpaMapper workoutJpaMapper;
  private final UserPort userPort;

  @Override
  public Workout save(final Workout workout) {
    // Validate that the user exists before saving the workout
    userPort
        .findById(workout.getUserId())
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "User not found with ID: " + workout.getUserId()));

    final var entity = workoutJpaMapper.toJpaEntity(workout);
    final var saved = workoutJpaRepository.save(entity);
    return workoutJpaMapper.toDomain(saved);
  }

  @Override
  public Optional<Workout> findById(final Long id) {
    return workoutJpaRepository.findById(id).map(workoutJpaMapper::toDomain);
  }

  @Override
  public List<Workout> findByUserId(final Long userId) {
    return workoutJpaRepository.findByUserId(userId).stream()
        .map(workoutJpaMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Workout> findAll() {
    return workoutJpaRepository.findAll().stream()
        .map(workoutJpaMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(final Long id) {
    workoutJpaRepository.deleteById(id);
  }
}


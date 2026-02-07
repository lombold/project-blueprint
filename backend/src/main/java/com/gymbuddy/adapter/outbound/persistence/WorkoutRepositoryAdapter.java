package com.gymbuddy.adapter.outbound.persistence;

import com.gymbuddy.adapter.outbound.persistence.mapper.WorkoutJpaMapper;
import com.gymbuddy.application.port.WorkoutPort;
import com.gymbuddy.domain.entity.Workout;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkoutRepositoryAdapter implements WorkoutPort {

  private final WorkoutJpaRepository workoutJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final WorkoutJpaMapper workoutJpaMapper;

  @Override
  public Workout save(Workout workout) {
    WorkoutJpaEntity entity = workoutJpaMapper.toJpaEntity(workout);
    WorkoutJpaEntity saved = workoutJpaRepository.save(entity);
    return workoutJpaMapper.toDomain(saved);
  }

  @Override
  public Optional<Workout> findById(Long id) {
    return workoutJpaRepository.findById(id).map(workoutJpaMapper::toDomain);
  }

  @Override
  public List<Workout> findByUserId(Long userId) {
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
  public void deleteById(Long id) {
    workoutJpaRepository.deleteById(id);
  }
}


package com.gymbuddy.adapter.outbound.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Workout entities.
 * This is an adapter implementing the WorkoutPort interface.
 */
@Repository
public interface WorkoutJpaRepository extends JpaRepository<WorkoutJpaEntity, Long> {

  /**
   * Finds all workouts for a specific user.
   *
   * @param userId the user ID
   * @return a list of workouts for the user
   */
  List<WorkoutJpaEntity> findByUserId(Long userId);
}


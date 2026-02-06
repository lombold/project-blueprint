package com.gymbuddy.adapter.outbound.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for Exercise.
 * This is an adapter class that bridges the domain Exercise entity with the database.
 */
@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "workout_id")
  private Long workoutId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "workout_id", insertable = false, updatable = false)
  private WorkoutJpaEntity workout;

  @Column(nullable = false)
  private String name;

  private Integer sets;

  private Integer reps;

  @Column(name = "weight_kg")
  private Double weightKg;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @Column(name = "order_index")
  private Integer orderIndex;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  /**
   * Converts this JPA entity to a domain Exercise entity.
   *
   * @return the domain Exercise entity
   */
  public com.gymbuddy.domain.entity.Exercise toDomainEntity() {
    return com.gymbuddy.domain.entity.Exercise.builder()
        .id(this.id)
        .workoutId(this.workoutId)
        .name(this.name)
        .sets(this.sets)
        .reps(this.reps)
        .weightKg(this.weightKg)
        .notes(this.notes)
        .orderIndex(this.orderIndex)
        .createdAt(this.createdAt)
        .build();
  }
}

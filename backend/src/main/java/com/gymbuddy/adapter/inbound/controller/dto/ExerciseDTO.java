package com.gymbuddy.adapter.inbound.controller.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for Exercise.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDTO {

  private Long id;
  private Long workoutId;
  private String name;
  private Integer sets;
  private Integer reps;
  private Double weightKg;
  private String notes;
  private Integer orderIndex;
  private LocalDateTime createdAt;
}

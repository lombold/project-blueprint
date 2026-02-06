package com.gymbuddy.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User domain entity.
 * Represents a user in the gym-buddy application.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  private Long id;
  private String username;
  private String email;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Builder.Default
  private List<Workout> workouts = new ArrayList<>();

  /**
   * Validates that the user has valid username and email.
   *
   * @throws IllegalArgumentException if username or email is invalid
   */
  public void validate() {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email cannot be empty");
    }
    if (!email.contains("@")) {
      throw new IllegalArgumentException("Email must be valid");
    }
  }
}

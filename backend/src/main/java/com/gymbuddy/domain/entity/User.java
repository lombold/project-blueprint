package com.gymbuddy.domain.entity;

import com.gymbuddy.domain.value.UserId;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  private UserId id;
  private String username;
  private String email;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

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

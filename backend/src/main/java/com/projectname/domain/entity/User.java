package com.projectname.domain.entity;

import com.projectname.domain.value.UserId;
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

  private static final int MAX_FIELD_LENGTH = 255;

  public void validate() {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }
    if (username.length() > MAX_FIELD_LENGTH) {
      throw new IllegalArgumentException("Username must not exceed " + MAX_FIELD_LENGTH + " characters");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email cannot be empty");
    }
    if (email.length() > MAX_FIELD_LENGTH) {
      throw new IllegalArgumentException("Email must not exceed " + MAX_FIELD_LENGTH + " characters");
    }
    if (!email.contains("@")) {
      throw new IllegalArgumentException("Email must be valid");
    }
  }
}

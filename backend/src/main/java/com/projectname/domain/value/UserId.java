package com.projectname.domain.value;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value object representing a user identifier.
 */
public final class UserId implements Serializable {

  private final Long value;

  private UserId(final Long value) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException("User ID must be a positive number");
    }
    this.value = value;
  }

  public static UserId of(final Long value) {
    return new UserId(value);
  }

  public Long getValue() {
    return value;
  }

  public Long toLong() {
    return value;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    final UserId userId = (UserId) other;
    return Objects.equals(value, userId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "UserId{" + "value=" + value + '}';
  }
}


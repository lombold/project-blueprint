package com.gymbuddy.adapter.outbound.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for User.
 * This is an adapter class that bridges the domain User entity with the database.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<WorkoutJpaEntity> workouts = new ArrayList<>();

  /**
   * Converts this JPA entity to a domain User entity.
   *
   * @return the domain User entity
   */
  public com.gymbuddy.domain.entity.User toDomainEntity() {
    return com.gymbuddy.domain.entity.User.builder()
        .id(this.id)
        .username(this.username)
        .email(this.email)
        .createdAt(this.createdAt)
        .updatedAt(this.updatedAt)
        .build();
  }
}

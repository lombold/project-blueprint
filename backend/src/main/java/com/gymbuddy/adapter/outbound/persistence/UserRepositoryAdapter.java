package com.gymbuddy.adapter.outbound.persistence;

import com.gymbuddy.application.port.UserPort;
import com.gymbuddy.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adapter implementation of UserPort using Spring Data JPA.
 * Bridges the application layer with the persistence layer.
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserPort {

  private final UserJpaRepository userJpaRepository;

  @Override
  public User save(User user) {
    UserJpaEntity entity = toJpaEntity(user);
    UserJpaEntity saved = userJpaRepository.save(entity);
    return saved.toDomainEntity();
  }

  @Override
  public Optional<User> findById(Long id) {
    return userJpaRepository.findById(id).map(UserJpaEntity::toDomainEntity);
  }

  @Override
  public List<User> findAll() {
    return userJpaRepository.findAll().stream()
        .map(UserJpaEntity::toDomainEntity)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<User> findByUsername(String username) {
    UserJpaEntity entity = userJpaRepository.findByUsername(username);
    return Optional.ofNullable(entity).map(UserJpaEntity::toDomainEntity);
  }

  @Override
  public void deleteById(Long id) {
    userJpaRepository.deleteById(id);
  }

  private UserJpaEntity toJpaEntity(User user) {
    return UserJpaEntity.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }
}

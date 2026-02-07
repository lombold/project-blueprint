package com.gymbuddy.adapter.outbound.persistence;

import com.gymbuddy.adapter.outbound.persistence.mapper.UserJpaMapper;
import com.gymbuddy.application.port.UserPort;
import com.gymbuddy.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserPort {

  private final UserJpaRepository userJpaRepository;
  private final UserJpaMapper userJpaMapper;

  @Override
  public User save(User user) {
    UserJpaEntity entity = userJpaMapper.toJpaEntity(user);
    UserJpaEntity saved = userJpaRepository.save(entity);
    return userJpaMapper.toDomain(saved);
  }

  @Override
  public Optional<User> findById(Long id) {
    return userJpaRepository.findById(id).map(userJpaMapper::toDomain);
  }

  @Override
  public List<User> findAll() {
    return userJpaRepository.findAll().stream()
        .map(userJpaMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<User> findByUsername(String username) {
    UserJpaEntity entity = userJpaRepository.findByUsername(username);
    return Optional.ofNullable(entity).map(userJpaMapper::toDomain);
  }

  @Override
  public void deleteById(Long id) {
    userJpaRepository.deleteById(id);
  }
}

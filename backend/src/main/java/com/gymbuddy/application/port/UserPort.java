package com.gymbuddy.application.port;

import com.gymbuddy.domain.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port for user persistence operations.
 * This interface defines the contract for user repository implementations.
 */
public interface UserPort {

  /**
   * Saves a user (create or update).
   *
   * @param user the user to save
   * @return the saved user with ID populated
   */
  User save(User user);

  /**
   * Retrieves a user by ID.
   *
   * @param id the user ID
   * @return an Optional containing the user if found
   */
  Optional<User> findById(Long id);

  /**
   * Retrieves all users.
   *
   * @return a list of all users
   */
  List<User> findAll();

  /**
   * Retrieves a user by username.
   *
   * @param username the username
   * @return an Optional containing the user if found
   */
  Optional<User> findByUsername(String username);

  /**
   * Deletes a user by ID.
   *
   * @param id the user ID
   */
  void deleteById(Long id);
}

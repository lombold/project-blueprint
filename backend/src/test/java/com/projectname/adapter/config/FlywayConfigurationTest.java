package com.projectname.adapter.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Tests for Flyway migration configuration conventions.
 */
class FlywayConfigurationTest {

  @Test
  void shouldUseIncrementalMigrationsByDefaultAndCurrentMigrationsForLocalProfile() throws IOException {
    // Given
    final var applicationConfig = readResource("application.yml");

    // Then
    assertTrue(applicationConfig.contains("ddl-auto: validate"));
    assertTrue(applicationConfig.contains("locations: classpath:db/migration/incremental"));
    assertTrue(applicationConfig.contains("validate-on-migrate: true"));
    assertTrue(applicationConfig.contains("on-profile: local"));
    assertTrue(applicationConfig.contains("locations: classpath:db/migration/current"));
  }

  @Test
  void shouldKeepCurrentSnapshotSeparateFromIncrementalHistory() throws IOException {
    // Given
    final var currentSnapshot = readResource("db/migration/current/R__current_schema.sql");
    final var firstMigration = readResource("db/migration/incremental/V1__create_users.sql");

    // Then
    assertTrue(currentSnapshot.contains("DROP TABLE IF EXISTS users"));
    assertTrue(currentSnapshot.contains("CREATE TABLE users"));
    assertTrue(firstMigration.contains("CREATE TABLE users"));
    assertFalse(firstMigration.contains("DROP TABLE"));
  }

  private String readResource(final String path) throws IOException {
    final var resource = new ClassPathResource(path);
    assertTrue(resource.exists(), () -> "Expected classpath resource to exist: " + path);
    return resource.getContentAsString(StandardCharsets.UTF_8);
  }
}

package com.projectname.adapter.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Integration tests for Flyway startup migrations.
 */
@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:flyway-startup;DB_CLOSE_DELAY=-1",
      "spring.datasource.username=sa",
      "spring.datasource.password="
    })
class FlywayStartupIntegrationTest {

  @Autowired
  private Flyway flyway;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void shouldRunIncrementalMigrationBeforeHibernateValidation() {
    // Given
    final var appliedMigrations = flyway.info().applied();

    // When
    final var userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);

    // Then
    assertEquals(3, userCount);
    assertEquals(1, appliedMigrations.length);
    assertEquals("1", appliedMigrations[0].getVersion().getVersion());
    assertEquals("V1__create_users.sql", appliedMigrations[0].getScript());
  }
}

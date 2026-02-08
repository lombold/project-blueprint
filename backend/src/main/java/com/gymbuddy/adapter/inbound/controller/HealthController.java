package com.gymbuddy.adapter.inbound.controller;

import com.gymbuddy.adapter.inbound.controller.dto.HealthStatus;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check endpoint for the application.
 * Returns application status and version information.
 */
@RestController
public class HealthController implements HealthApi {

  /**
   * Returns health status of the application.
   *
   * @return health status response
   */
  @Override
  public ResponseEntity<HealthStatus> getHealth() {
    final var status = new HealthStatus()
        .status("UP")
        .application("gym-buddy")
        .version("1.0.0")
        .timestamp(LocalDateTime.now());
    return ResponseEntity.ok(status);
  }
}

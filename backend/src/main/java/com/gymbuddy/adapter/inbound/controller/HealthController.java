package com.gymbuddy.adapter.inbound.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check endpoint for the application.
 * Returns application status and version information.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

  /**
   * Returns health status of the application.
   *
   * @return health status response
   */
  @GetMapping
  public ResponseEntity<?> health() {
    return ResponseEntity.ok().body(
        java.util.Map.of(
            "status", "UP",
            "application", "gym-buddy",
            "version", "1.0.0",
            "timestamp", java.time.LocalDateTime.now()));
  }
}

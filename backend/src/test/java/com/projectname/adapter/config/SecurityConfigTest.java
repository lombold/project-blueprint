package com.projectname.adapter.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/** Unit tests for API CORS security configuration. */
class SecurityConfigTest {

    @Test
    void shouldConfigureExplicitOriginsForApiRequests() {
        // Given
        final var allowedOrigins =
                List.of("http://localhost:4200", "http://localhost:8100", "capacitor://localhost", "https://localhost");
        final var securityConfig = new SecurityConfig(new CorsProperties(allowedOrigins));
        final var request = new MockHttpServletRequest("GET", "/api/users");

        // When
        final var corsConfiguration = securityConfig.corsConfigurationSource().getCorsConfiguration(request);

        // Then
        assertEquals(allowedOrigins, corsConfiguration.getAllowedOrigins());
        assertFalse(corsConfiguration.getAllowedOrigins().contains("*"));
        assertEquals(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"), corsConfiguration.getAllowedMethods());
        assertEquals(List.of("*"), corsConfiguration.getAllowedHeaders());
        assertEquals(Boolean.FALSE, corsConfiguration.getAllowCredentials());
    }

    @Test
    void shouldLimitCorsConfigurationToApiRequests() {
        // Given
        final var securityConfig = new SecurityConfig(new CorsProperties(List.of("https://localhost")));
        final var request = new MockHttpServletRequest("GET", "/actuator/health");

        // When
        final var corsConfiguration = securityConfig.corsConfigurationSource().getCorsConfiguration(request);

        // Then
        assertNull(corsConfiguration);
    }
}

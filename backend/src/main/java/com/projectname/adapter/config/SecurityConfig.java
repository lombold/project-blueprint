package com.projectname.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Content Security Policy for the Angular SPA.
     *
     * <p>{@code style-src 'unsafe-inline'} is required because Angular injects component
     * styles at runtime. Scripts remain restricted to same-origin bundles.</p>
     */
    private static final String CONTENT_SECURITY_POLICY =
            "default-src 'self'; "
            + "script-src 'self'; "
            + "style-src 'self' 'unsafe-inline'; "
            + "img-src 'self' data:; "
            + "font-src 'self'; "
            + "connect-src 'self'; "
            + "object-src 'none'; "
            + "frame-ancestors 'self'; "
            + "base-uri 'self'; "
            + "form-action 'self'";

    /**
     * Dev-only filter chain for the H2 console ({@code dev} profile, see application-dev.yml).
     * The console relies on inline scripts and frames, which the strict CSP of the main
     * chain would block.
     */
    @Bean
    @Profile("dev")
    @Order(1)
    SecurityFilterChain h2ConsoleFilterChain(final HttpSecurity http) {
        return http
                .securityMatcher("/h2-console/**")
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(final HttpSecurity http) {
        return http
                // CSRF protection is not needed: the API is stateless and uses no
                // cookie-based authentication. Revisit if session/cookie auth is added.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(CONTENT_SECURITY_POLICY))
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .referrerPolicy(referrer -> referrer.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))
                // NOTE: blueprint default — every endpoint is public. Tighten this
                // as soon as the project has an authentication concept.
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}

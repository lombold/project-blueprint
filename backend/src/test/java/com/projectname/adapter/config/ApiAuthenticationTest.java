package com.projectname.adapter.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/** MVC tests for OAuth2 resource-server authentication. */
@SpringJUnitWebConfig(ApiAuthenticationTest.TestConfig.class)
@TestPropertySource(properties = "app.cors.allowed-origins[0]=http://localhost:4200")
class ApiAuthenticationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void shouldRejectUnauthenticatedApiRequest() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/test")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAuthenticatedApiRequest() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/test").with(jwt())).andExpect(status().isOk());
    }

    @Test
    void shouldAllowUnauthenticatedHealthRequest() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/health")).andExpect(status().isOk());
    }

    @Configuration
    @EnableWebMvc
    @Import({SecurityConfig.class, TestApiController.class})
    static class TestConfig {

        @Bean
        JwtDecoder jwtDecoder() {
            return Mockito.mock(JwtDecoder.class);
        }
    }

    @RestController
    static class TestApiController {

        @GetMapping("/api/test")
        String api() {
            return "ok";
        }

        @GetMapping("/api/health")
        String health() {
            return "ok";
        }
    }
}

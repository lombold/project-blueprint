package com.projectname.adapter.inbound.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** Unit tests for SpaForwardController. */
class SpaForwardControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SpaForwardController()).build();
    }

    @Test
    void shouldForwardUiRootWithTrailingSlash() throws Exception {
        // Given

        // When
        final var response = mockMvc.perform(get("/ui/"));

        // Then
        response.andExpect(status().isOk()).andExpect(forwardedUrl("/ui/index.html"));
    }
}

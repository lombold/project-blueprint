package com.projectname.adapter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures Spring MVC to serve the Angular SPA from /ui/ and redirect the root path.
 *
 * <p>Static files in {@code classpath:/static/ui/} (JS, CSS, images, etc.) are served
 * automatically by Spring Boot's default resource handling and take precedence over
 * the forwarding rules below.</p>
 */
@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        // Redirect root to the Angular app
        registry.addRedirectViewController("/", "/ui/");
    }
}

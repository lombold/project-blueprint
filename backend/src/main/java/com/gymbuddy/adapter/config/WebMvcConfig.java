package com.gymbuddy.adapter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for the application.
 * Handles static resource serving, CORS, and other web settings.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  /**
   * Registers resource handlers for serving static files.
   * Angular builds will be served from /ui endpoint.
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/ui/**")
        .addResourceLocations("classpath:/static/ui/")
        .setCachePeriod(3600);

    registry
        .addResourceHandler("/ui/")
        .addResourceLocations("classpath:/static/ui/index.html")
        .setCachePeriod(0);
  }

  /**
   * Configures CORS for the application.
   * Allows requests from the Angular development server and production.
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/api/**")
        .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
  }
}

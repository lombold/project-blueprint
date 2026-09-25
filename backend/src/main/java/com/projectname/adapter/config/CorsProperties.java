package com.projectname.adapter.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configures the browser and native WebView origins allowed to call the API.
 *
 * @param allowedOrigins origins permitted by the API's CORS policy
 */
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(List<String> allowedOrigins) {

    public CorsProperties {
        allowedOrigins = List.copyOf(allowedOrigins);
    }
}

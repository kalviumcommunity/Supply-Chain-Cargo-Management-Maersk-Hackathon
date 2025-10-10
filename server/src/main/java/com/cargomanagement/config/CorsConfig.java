package com.cargomanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow frontend origins
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:5174"
        ));
        
       
        configuration.setAllowCredentials(true);
        
       
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Expose headers
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
        
        // How long the response from a pre-flight request can be cached
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

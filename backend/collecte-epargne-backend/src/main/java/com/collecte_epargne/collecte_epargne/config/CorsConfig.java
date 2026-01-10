package com.collecte_epargne.collecte_epargne.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Autoriser toutes les origines en développement (localhost sur n'importe quel port)
        config.addAllowedOriginPattern("*");
        
        // Autoriser toutes les méthodes HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Autoriser tous les headers
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Autoriser les credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Exposer tous les headers dans la réponse
        config.setExposedHeaders(Arrays.asList("*"));
        
        // Durée de cache de la requête preflight (OPTIONS)
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}


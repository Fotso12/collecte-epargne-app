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
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Autoriser les origines pour Angular (localhost:4200) et Flutter en développement
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:4200",  // Port par défaut Angular
            "http://localhost:3000",  // Port commun pour Flutter
            "http://localhost:8080",  // Port alternatif
            "http://127.0.0.1:4200", // Alternative localhost
            "http://127.0.0.1:3000"  // Alternative localhost
        ));

        // Autoriser toutes les méthodes HTTP
        corsConfiguration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Autoriser tous les en-têtes
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));

        // Autoriser les credentials (important pour l'authentification)
        corsConfiguration.setAllowCredentials(true);

        // Exposer les en-têtes courants
        corsConfiguration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept"
        ));

        // Mettre en cache la réponse preflight pendant 1 heure
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}

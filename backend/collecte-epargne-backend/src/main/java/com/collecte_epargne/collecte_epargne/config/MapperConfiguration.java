package com.collecte_epargne.collecte_epargne.config;

import com.collecte_epargne.collecte_epargne.mappers.AgenceZoneMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Fournit des beans MapStruct comme fallback si l'implémentation générée
 * n'est pas disponible via le component-scan (cas de génération/compilation
 * partielle dans certains environnements).
 */
@Configuration
public class MapperConfiguration {

    @Bean
    public AgenceZoneMapper agenceZoneMapper() {
        return Mappers.getMapper(AgenceZoneMapper.class);
    }

}

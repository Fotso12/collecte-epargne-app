package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.AgenceZoneDto;
import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface AgenceZoneMapper {
    // Conversion Entité vers DTO
    AgenceZoneDto toDto(AgenceZone agenceZone);

    // Conversion DTO vers Entité (utile pour la création ou mise à jour, mais l'ID doit être géré manuellement pour les Mappings d'ID)
    AgenceZone toEntity(AgenceZoneDto agenceZoneDto);
}
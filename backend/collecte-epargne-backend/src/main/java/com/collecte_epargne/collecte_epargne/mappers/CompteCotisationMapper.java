package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.CompteCotisationDto;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.CompteCotisation;
import com.collecte_epargne.collecte_epargne.entities.PlanCotisation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CompteCotisationMapper {

    @Mapping(source = "compte", target = "idCompte")
    @Mapping(source = "planCotisation", target = "idPlanCotisation")
    CompteCotisationDto toDto(CompteCotisation compteCotisation);

    // --- Conversion Helper (Entité -> ID)
    default String compteToId(Compte compte) {
        return compte != null ? compte.getIdCompte() : null;
    }

    default String planCotisationToId(PlanCotisation planCotisation) {
        return planCotisation != null ? planCotisation.getIdPlan() : null;
    }

    // --- Conversion DTO -> Entité (Ignorer les objets relationnels)
    @Mapping(target = "compte", ignore = true)
    @Mapping(target = "planCotisation", ignore = true)
    CompteCotisation toEntity(CompteCotisationDto compteCotisationDto);
}
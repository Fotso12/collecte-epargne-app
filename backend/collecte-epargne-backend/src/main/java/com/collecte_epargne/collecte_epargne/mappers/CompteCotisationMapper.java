package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.CompteCotisationDto;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.CompteCotisation;
import com.collecte_epargne.collecte_epargne.entities.PlanCotisation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CompteCotisationMapper {

    @Mapping(source = "compte", target = "idCompte", qualifiedByName = "compteToId")
    @Mapping(source = "planCotisation", target = "idPlanCotisation", qualifiedByName = "planCotisationToId")
    CompteCotisationDto toDto(CompteCotisation compteCotisation);

    @Named("compteToId")
    default String compteToId(Compte compte) {
        return compte != null ? compte.getIdCompte() : null;
    }

    @Named("planCotisationToId")
    default String planCotisationToId(PlanCotisation planCotisation) {
        return planCotisation != null ? planCotisation.getIdPlan() : null;
    }

    @Mapping(target = "compte", ignore = true)
    @Mapping(target = "planCotisation", ignore = true)
    CompteCotisation toEntity(CompteCotisationDto compteCotisationDto);
}

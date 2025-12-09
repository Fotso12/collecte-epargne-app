package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.PlanCotisationDto;
import com.collecte_epargne.collecte_epargne.entities.PlanCotisation;
import org.mapstruct.Mapper;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface PlanCotisationMapper {

    PlanCotisationDto toDto(PlanCotisation planCotisation);

    PlanCotisation toEntity(PlanCotisationDto planCotisationDto);
}
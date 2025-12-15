package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.RapportCollecteurDto;
import com.collecte_epargne.collecte_epargne.entities.RapportCollecteur;
import org.mapstruct.Mapper;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RapportCollecteurMapper {

    RapportCollecteurDto toDto(RapportCollecteur rapportCollecteur);

    RapportCollecteur toEntity(RapportCollecteurDto rapportCollecteurDto);
}
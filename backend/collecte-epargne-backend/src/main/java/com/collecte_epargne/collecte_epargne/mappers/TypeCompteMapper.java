package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.TypeCompteDto;
import com.collecte_epargne.collecte_epargne.entities.TypeCompte;
import org.mapstruct.Mapper;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface TypeCompteMapper {

    TypeCompteDto toDto(TypeCompte typeCompte);

    TypeCompte toEntity(TypeCompteDto typeCompteDto);
}
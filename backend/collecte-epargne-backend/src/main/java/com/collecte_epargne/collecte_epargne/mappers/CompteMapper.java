package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.CompteDto;
import com.collecte_epargne.collecte_epargne.entities.Client;
import com.collecte_epargne.collecte_epargne.entities.Compte;
import com.collecte_epargne.collecte_epargne.entities.TypeCompte;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CompteMapper {

    @Mapping(source = "client", target = "codeClient")
    @Mapping(source = "typeCompte", target = "idTypeCompte")
    CompteDto toDto(Compte compte);

    // --- Conversion Helper (Entité -> ID)
    default String clientToCode(Client client) {
        return client != null ? client.getCodeClient() : null;
    }

    default Integer typeCompteToId(TypeCompte typeCompte) {
        return typeCompte != null ? typeCompte.getId() : null;
    }

    // --- Conversion DTO -> Entité (Ignorer les objets relationnels et collections)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "typeCompte", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "plansDeCotisation", ignore = true)
    Compte toEntity(CompteDto compteDto);
}
package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.ClientDto;
import com.collecte_epargne.collecte_epargne.entities.Client;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "utilisateur", target = "loginUtilisateur", qualifiedByName = "utilisateurToLogin")
    @Mapping(source = "collecteurAssigne", target = "codeCollecteurAssigne", qualifiedByName = "employeToCode")
    ClientDto toDto(Client client);

    @Named("utilisateurToLogin")
    default String utilisateurToLogin(Utilisateur utilisateur) {
        return utilisateur != null ? utilisateur.getLogin() : null;
    }

    @Named("employeToCode")
    default Integer employeToCode(Employe employe) {
        return employe != null ? employe.getIdEmploye() : null;
    }

    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "collecteurAssigne", ignore = true)
    @Mapping(target = "comptes", ignore = true)
    Client toEntity(ClientDto clientDto);
}

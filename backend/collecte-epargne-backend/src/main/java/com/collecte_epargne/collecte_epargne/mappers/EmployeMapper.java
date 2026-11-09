package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.EmployeDto;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EmployeMapper {

    @Mapping(source = "utilisateur", target = "loginUtilisateur", qualifiedByName = "utilisateurToLogin")
    @Mapping(source = "agenceZone.idAgence", target = "idAgence")
    @Mapping(source = "superviseur", target = "idSuperviseur", qualifiedByName = "employeToId")
    @Mapping(source = "utilisateur.nom", target = "nom")
    @Mapping(source = "utilisateur.prenom", target = "prenom")
    @Mapping(source = "utilisateur.email", target = "email")
    @Mapping(source = "utilisateur.telephone", target = "telephone")
    EmployeDto toDto(Employe employe);

    @Named("utilisateurToLogin")
    default String utilisateurToLogin(Utilisateur utilisateur) {
        return (utilisateur != null) ? utilisateur.getLogin() : null;
    }

    @Named("employeToId")
    default String employeToId(Employe superviseur) {
        return (superviseur != null) ? String.valueOf(superviseur.getIdEmploye()) : null;
    }

    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "agenceZone", ignore = true)
    @Mapping(target = "superviseur", ignore = true)
    @Mapping(target = "equipeSupervisee", ignore = true)
    @Mapping(target = "clientsAssignes", ignore = true)
    Employe toEntity(EmployeDto employeDto);
}
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
    @Mapping(source = "collecteurAssigne", target = "nomCollecteur", qualifiedByName = "employeToNomComplet")
    @Mapping(target = "idAgence", source = "collecteurAssigne.agenceZone.idAgence")
    @Mapping(source = "utilisateur.nom", target = "nom")
    @Mapping(source = "utilisateur.prenom", target = "prenom")
    @Mapping(source = "utilisateur.telephone", target = "telephone")
    @Mapping(source = "utilisateur.email", target = "email")
    @Mapping(source = "utilisateur.statut", target = "statut")
    @Mapping(source = "utilisateur.dateCreation", target = "dateCreation")
    ClientDto toDto(Client client);

    @Named("utilisateurToLogin")
    default String utilisateurToLogin(Utilisateur utilisateur) {
        return utilisateur != null ? utilisateur.getLogin() : null;
    }

    @Named("employeToCode")
    default Integer employeToCode(Employe employe) {
        return employe != null ? employe.getIdEmploye() : null;
    }

    // AJOUTEZ CE MÉTHODE :
    @Named("employeToNomComplet")
    default String employeToNomComplet(Employe employe) {
        if (employe == null || employe.getUtilisateur() == null) {
            return "Non assigné";
        }
        return employe.getUtilisateur().getNom() + " " + employe.getUtilisateur().getPrenom();
    }

    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "collecteurAssigne", ignore = true)
    @Mapping(target = "comptes", ignore = true)
    Client toEntity(ClientDto clientDto);
}

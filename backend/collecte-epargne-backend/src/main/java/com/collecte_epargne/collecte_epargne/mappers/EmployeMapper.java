package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.EmployeDto;
import com.collecte_epargne.collecte_epargne.entities.AgenceZone;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface EmployeMapper {

    @Mapping(source = "utilisateur", target = "loginUtilisateur")
    @Mapping(source = "agenceZone", target = "idAgenceZone")
    @Mapping(source = "superviseur", target = "idSuperviseur")
    EmployeDto toDto(Employe employe);

    // --- Conversion Helper (Entité -> ID)
    default String utilisateurToLogin(Utilisateur utilisateur) {
        return utilisateur != null ? utilisateur.getLogin() : null;
    }

    default String agenceZoneToId(AgenceZone agenceZone) {
        return agenceZone != null ? agenceZone.getIdAgence() : null;
    }

    default String employeToId(Employe superviseur) {
        return superviseur != null ? superviseur.getIdEmploye() : null;
    }

    // --- Conversion DTO -> Entité (Ignorer les objets relationnels et collections)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "agenceZone", ignore = true)
    @Mapping(target = "superviseur", ignore = true)
    @Mapping(target = "equipeSupervisee", ignore = true)
    @Mapping(target = "clientsAssignes", ignore = true)
    Employe toEntity(EmployeDto employeDto);
}
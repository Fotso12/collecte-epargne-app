package com.collecte_epargne.collecte_epargne.mappers;

import com.collecte_epargne.collecte_epargne.dtos.UtilisateurDto;
import com.collecte_epargne.collecte_epargne.entities.Client;
import com.collecte_epargne.collecte_epargne.entities.Employe;
import com.collecte_epargne.collecte_epargne.entities.Role;
import com.collecte_epargne.collecte_epargne.entities.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    @Mapping(source = "role", target = "idRole")
    @Mapping(source = "employe", target = "idEmploye")
    @Mapping(source = "client", target = "codeClient")
    UtilisateurDto toDto(Utilisateur utilisateur);

    // --- Conversion Helper (Entité -> ID)
    default Integer roleToId(Role role) {
        return role != null ? role.getId() : null;
    }

    default Integer employeToId(Employe employe) {
        return employe != null ? employe.getIdEmploye() : null;
    }

    default String clientToCode(Client client) {
        return client != null ? client.getCodeClient() : null;
    }

    // --- Conversion DTO -> Entité (Ignorer les objets relationnels)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "employe", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "password", ignore = true)
    // Le mot de passe (si présent dans l'entité) doit être géré par le service (hachage)
    Utilisateur toEntity(UtilisateurDto utilisateurDto);
}
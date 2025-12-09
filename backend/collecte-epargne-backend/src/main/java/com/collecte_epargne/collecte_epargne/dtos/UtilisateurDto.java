package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO pour Utilisateur
 */
@Value
public class UtilisateurDto implements Serializable {
    @Size(max = 50)
    String login;

    // Remplacer Role par son ID
    @NotNull
    Integer idRole;

    // Remplacer Employe par son ID
    String idEmploye;

    // Remplacer Client par son ID
    String codeClient;

    @Size(max = 50)
    @NotNull
    String nom;

    @Size(max = 50)
    @NotNull
    String prenom;

    @Size(max = 20)
    @NotNull
    String telephone;

    @Size(max = 100)
    @NotNull
    String email;

    // Le champ 'password' est exclu pour la sécurité

    StatutGenerique statut;

    Instant dateCreation;
}
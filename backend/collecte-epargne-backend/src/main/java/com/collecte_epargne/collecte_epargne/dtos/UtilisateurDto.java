package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurDto implements Serializable {
    @Size(max = 50)
    @NotNull
    private String login;

    @NotNull
    private Integer idRole;

    // --- Champs Relationnels (ajoutés pour le DTO complet) ---
    private String idEmploye;
    private String codeClient;
    // --------------------------------------------------------

    @Size(max = 50)
    @NotNull
    private String nom;

    @Size(max = 50)
    @NotNull
    private String prenom;

    @Size(max = 20)
    @NotNull
    private String telephone;

    @Size(max = 100)
    @NotNull
    private String email;

    // Le champ 'password' est EXCLU de ce DTO de sortie/mise à jour

    private StatutGenerique statut;

    private Instant dateCreation;
}
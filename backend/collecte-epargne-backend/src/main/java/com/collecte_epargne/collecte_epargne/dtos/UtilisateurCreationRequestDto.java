package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutGenerique;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UtilisateurCreationRequestDto {

    @Size(max = 50)
    @NotNull
    private String login;

    @NotNull
    private Integer idRole;

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

    @Size(min = 6, max = 255) // Ajout d'une contrainte de taille pour le password
    @NotNull
    private String password; // Le mot de passe en clair (non haché)

    private StatutGenerique statut;
}
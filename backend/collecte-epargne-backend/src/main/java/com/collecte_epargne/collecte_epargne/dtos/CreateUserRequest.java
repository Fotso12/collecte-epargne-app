package com.collecte_epargne.collecte_epargne.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour créer un utilisateur (caissier, collecteur, superviseur, auditeur)
 * Par un admin
 */
@Getter
@Setter
public class CreateUserRequest {
    @Size(max = 150)
    @NotNull
    private String fullName;

    @Size(max = 150)
    @NotNull
    private String email;

    @Size(max = 40)
    @NotNull
    private String phone;

    @Size(min = 6, max = 255)
    @NotNull
    private String password;

    @NotNull
    private String roleCode; // "caissier", "collector", "supervisor", "auditor"

    @NotNull
    private Long institutionId;

    // Champs spécifiques pour collecteur
    @Size(max = 50)
    private String badgeCode; // Code badge du collecteur

    @Size(max = 100)
    private String zone; // Zone d'intervention

    // Champs spécifiques pour caissier
    @Size(max = 50)
    private String matricule; // Matricule du caissier
}





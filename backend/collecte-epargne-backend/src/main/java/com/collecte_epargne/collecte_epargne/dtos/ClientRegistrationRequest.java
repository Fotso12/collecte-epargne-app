package com.collecte_epargne.collecte_epargne.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour l'inscription d'un client
 * Les clients ne sont PAS des users - table séparée
 */
@Getter
@Setter
public class ClientRegistrationRequest {
    @Size(max = 150)
    @NotNull
    private String fullName;

    @Size(max = 40)
    @NotNull
    private String phone;

    @Size(max = 40)
    private String identityType; // CNI, Passport, etc.

    @Size(max = 80)
    private String identityNumber;

    private String address;

    // Matricule du collecteur parrain (optionnel)
    // Si "0000" ou vide, le client ne sera pas affilié à un collecteur
    @Size(max = 50)
    private String collectorMatricule;

    // Email pour la connexion
    @Size(max = 100)
    @NotNull
    private String email;

    // Mot de passe pour la connexion
    @Size(min = 6, max = 255)
    @NotNull
    private String password;

    // Institution par défaut (id=1)
    private Long institutionId = 1L;
}


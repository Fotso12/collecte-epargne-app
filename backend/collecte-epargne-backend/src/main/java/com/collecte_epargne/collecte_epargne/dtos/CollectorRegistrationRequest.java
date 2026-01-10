package com.collecte_epargne.collecte_epargne.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour l'inscription d'un collecteur
 * Crée un user + une entrée collector
 */
@Getter
@Setter
public class CollectorRegistrationRequest {
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

    @Size(max = 50)
    private String badgeCode; // Code badge du collecteur

    @Size(max = 100)
    private String zone; // Zone d'intervention

    // Institution par défaut (id=1)
    private Long institutionId = 1L;
}


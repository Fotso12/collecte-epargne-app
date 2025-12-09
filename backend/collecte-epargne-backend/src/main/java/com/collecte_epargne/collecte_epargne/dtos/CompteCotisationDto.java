package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour CompteCotisation
 */
@Value
public class CompteCotisationDto implements Serializable {
    @Size(max = 50)
    String id;

    @NotNull
    LocalDate dateAdhesion;

    BigDecimal montantTotalVerse;

    Integer nombreVersements;

    Integer nombreRetards;

    LocalDate prochaineEcheance;

    StatutPlanCotisation statut;

    // Remplacer Compte par son ID_COMPTE
    @NotNull
    String idCompte;

    // Remplacer PlanCotisation par son ID_PLAN
    @NotNull
    String idPlanCotisation;
}
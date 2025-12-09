package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.FrequenceCotisation;
import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour PlanCotisation
 */
@Value
public class PlanCotisationDto implements Serializable {
    @Size(max = 50)
    String idPlan;

    @Size(max = 100)
    @NotNull
    String nom;

    @NotNull
    BigDecimal montantAttendu;

    @NotNull
    FrequenceCotisation frequence;

    Integer dureeJours;

    @NotNull
    LocalDate dateDebut;

    LocalDate dateFin;

    BigDecimal tauxPenaliteRetard;

    StatutPlanCotisation statut;
}
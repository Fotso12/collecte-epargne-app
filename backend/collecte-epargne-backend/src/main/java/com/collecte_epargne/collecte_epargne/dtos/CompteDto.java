package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutCompte;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO pour Compte
 */
@Value
public class CompteDto implements Serializable {
    @Size(max = 50)
    String idCompte;

    @Size(max = 50)
    @NotNull
    String numCompte;

    BigDecimal solde;

    BigDecimal soldeDisponible;

    @NotNull
    LocalDate dateOuverture;

    Instant dateDerniereTransaction;

    BigDecimal tauxPenalite;

    BigDecimal tauxBonus;

    StatutCompte statut;

    String motifBlocage;

    LocalDate dateCloture;

    // Remplacer Client par son CODE_CLIENT
    @NotNull
    String codeClient;

    // Remplacer TypeCompte par son ID
    @NotNull
    Integer idTypeCompte;

    // Transactions et plans de cotisations omis
}
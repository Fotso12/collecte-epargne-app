package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RapportCollecteurDto implements Serializable {
    @Size(max = 50)
    String idRapport;

    @Size(max = 50)
    @NotNull
    String idEmploye; // L'ID de l'Employe est gardé directement

    @NotNull
    LocalDate dateRapport;

    BigDecimal totalDepot;

    BigDecimal totalRetrait;

    Integer nombreTransactions;

    Integer nombreClientsVisites;

    BigDecimal soldeCollecteur;

    StatutTransaction statutRapport;

    Instant dateGeneration;

    Instant dateValidation;

    String commentaireSuperviseur;
}
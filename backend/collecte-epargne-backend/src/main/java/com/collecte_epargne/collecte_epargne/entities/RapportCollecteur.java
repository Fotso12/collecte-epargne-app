package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "rapport_collecteur")
public class RapportCollecteur {
    @Id
    @Size(max = 50)
    @Column(name = "ID_RAPPORT", nullable = false, length = 50)
    private String idRapport;

    @Size(max = 50)
    @NotNull
    @Column(name = "ID_EMPLOYE", nullable = false, length = 50)
    private String idEmploye;

    @NotNull
    @Column(name = "DATE_RAPPORT", nullable = false)
    private LocalDate dateRapport;

    @Column(name = "TOTAL_DEPOT", precision = 15, scale = 2)
    private BigDecimal totalDepot;

    @Column(name = "TOTAL_RETRAIT", precision = 15, scale = 2)
    private BigDecimal totalRetrait;

    @Column(name = "NOMBRE_TRANSACTIONS")
    private Integer nombreTransactions;

    @Column(name = "NOMBRE_CLIENTS_VISITES")
    private Integer nombreClientsVisites;

    @Column(name = "SOLDE_COLLECTEUR", precision = 15, scale = 2)
    private BigDecimal soldeCollecteur;

    @Lob
    @Column(name = "STATUT_RAPPORT")
    private StatutTransaction statutRapport;

    @Column(name = "DATE_GENERATION")
    private Instant dateGeneration;

    @Column(name = "DATE_VALIDATION")
    private Instant dateValidation;

    @Lob
    @Column(name = "COMMENTAIRE_SUPERVISEUR")
    private String commentaireSuperviseur;

}
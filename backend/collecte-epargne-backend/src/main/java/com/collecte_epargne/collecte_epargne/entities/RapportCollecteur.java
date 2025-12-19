package com.collecte_epargne.collecte_epargne.entities;

import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;


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

    public String getIdRapport() {
        return idRapport;
    }

    public void setIdRapport(String idRapport) {
        this.idRapport = idRapport;
    }

    public String getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(String idEmploye) {
        this.idEmploye = idEmploye;
    }

    public LocalDate getDateRapport() {
        return dateRapport;
    }

    public void setDateRapport(LocalDate dateRapport) {
        this.dateRapport = dateRapport;
    }

    public BigDecimal getTotalDepot() {
        return totalDepot;
    }

    public void setTotalDepot(BigDecimal totalDepot) {
        this.totalDepot = totalDepot;
    }

    public BigDecimal getTotalRetrait() {
        return totalRetrait;
    }

    public void setTotalRetrait(BigDecimal totalRetrait) {
        this.totalRetrait = totalRetrait;
    }

    public Integer getNombreTransactions() {
        return nombreTransactions;
    }

    public void setNombreTransactions(Integer nombreTransactions) {
        this.nombreTransactions = nombreTransactions;
    }

    public Integer getNombreClientsVisites() {
        return nombreClientsVisites;
    }

    public void setNombreClientsVisites(Integer nombreClientsVisites) {
        this.nombreClientsVisites = nombreClientsVisites;
    }

    public BigDecimal getSoldeCollecteur() {
        return soldeCollecteur;
    }

    public void setSoldeCollecteur(BigDecimal soldeCollecteur) {
        this.soldeCollecteur = soldeCollecteur;
    }

    public StatutTransaction getStatutRapport() {
        return statutRapport;
    }

    public void setStatutRapport(StatutTransaction statutRapport) {
        this.statutRapport = statutRapport;
    }

    public Instant getDateGeneration() {
        return dateGeneration;
    }

    public void setDateGeneration(Instant dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public Instant getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(Instant dateValidation) {
        this.dateValidation = dateValidation;
    }

    public String getCommentaireSuperviseur() {
        return commentaireSuperviseur;
    }

    public void setCommentaireSuperviseur(String commentaireSuperviseur) {
        this.commentaireSuperviseur = commentaireSuperviseur;
    }

    public RapportCollecteur(String idRapport, String idEmploye, LocalDate dateRapport, BigDecimal totalDepot, BigDecimal totalRetrait, Integer nombreTransactions, Integer nombreClientsVisites, BigDecimal soldeCollecteur, StatutTransaction statutRapport, Instant dateGeneration, Instant dateValidation, String commentaireSuperviseur) {
        this.idRapport = idRapport;
        this.idEmploye = idEmploye;
        this.dateRapport = dateRapport;
        this.totalDepot = totalDepot;
        this.totalRetrait = totalRetrait;
        this.nombreTransactions = nombreTransactions;
        this.nombreClientsVisites = nombreClientsVisites;
        this.soldeCollecteur = soldeCollecteur;
        this.statutRapport = statutRapport;
        this.dateGeneration = dateGeneration;
        this.dateValidation = dateValidation;
        this.commentaireSuperviseur = commentaireSuperviseur;
    }

    public RapportCollecteur() {
    }
}
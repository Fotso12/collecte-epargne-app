package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;


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

    public RapportCollecteurDto(String idRapport, String idEmploye, LocalDate dateRapport, BigDecimal totalDepot, BigDecimal totalRetrait, Integer nombreTransactions, Integer nombreClientsVisites, BigDecimal soldeCollecteur, StatutTransaction statutRapport, Instant dateGeneration, Instant dateValidation, String commentaireSuperviseur) {
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

    public RapportCollecteurDto() {
    }

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
}
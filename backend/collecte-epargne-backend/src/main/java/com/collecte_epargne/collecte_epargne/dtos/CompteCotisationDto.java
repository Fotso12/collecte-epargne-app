package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;


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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDateAdhesion() {
        return dateAdhesion;
    }

    public void setDateAdhesion(LocalDate dateAdhesion) {
        this.dateAdhesion = dateAdhesion;
    }

    public BigDecimal getMontantTotalVerse() {
        return montantTotalVerse;
    }

    public void setMontantTotalVerse(BigDecimal montantTotalVerse) {
        this.montantTotalVerse = montantTotalVerse;
    }

    public Integer getNombreVersements() {
        return nombreVersements;
    }

    public void setNombreVersements(Integer nombreVersements) {
        this.nombreVersements = nombreVersements;
    }

    public Integer getNombreRetards() {
        return nombreRetards;
    }

    public void setNombreRetards(Integer nombreRetards) {
        this.nombreRetards = nombreRetards;
    }

    public LocalDate getProchaineEcheance() {
        return prochaineEcheance;
    }

    public void setProchaineEcheance(LocalDate prochaineEcheance) {
        this.prochaineEcheance = prochaineEcheance;
    }

    public StatutPlanCotisation getStatut() {
        return statut;
    }

    public void setStatut(StatutPlanCotisation statut) {
        this.statut = statut;
    }

    public String getIdCompte() {
        return idCompte;
    }

    public void setIdCompte(String idCompte) {
        this.idCompte = idCompte;
    }

    public String getIdPlanCotisation() {
        return idPlanCotisation;
    }

    public void setIdPlanCotisation(String idPlanCotisation) {
        this.idPlanCotisation = idPlanCotisation;
    }

    public CompteCotisationDto() {
    }

    public CompteCotisationDto(String id, LocalDate dateAdhesion, BigDecimal montantTotalVerse, Integer nombreVersements, Integer nombreRetards, LocalDate prochaineEcheance, StatutPlanCotisation statut, String idCompte, String idPlanCotisation) {
        this.id = id;
        this.dateAdhesion = dateAdhesion;
        this.montantTotalVerse = montantTotalVerse;
        this.nombreVersements = nombreVersements;
        this.nombreRetards = nombreRetards;
        this.prochaineEcheance = prochaineEcheance;
        this.statut = statut;
        this.idCompte = idCompte;
        this.idPlanCotisation = idPlanCotisation;
    }
}
package com.collecte_epargne.collecte_epargne.dtos;

import com.collecte_epargne.collecte_epargne.utils.FrequenceCotisation;
import com.collecte_epargne.collecte_epargne.utils.StatutPlanCotisation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;


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

    public PlanCotisationDto(String idPlan, String nom, BigDecimal montantAttendu, FrequenceCotisation frequence, Integer dureeJours, LocalDate dateDebut, LocalDate dateFin, BigDecimal tauxPenaliteRetard, StatutPlanCotisation statut) {
        this.idPlan = idPlan;
        this.nom = nom;
        this.montantAttendu = montantAttendu;
        this.frequence = frequence;
        this.dureeJours = dureeJours;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.tauxPenaliteRetard = tauxPenaliteRetard;
        this.statut = statut;
    }

    public PlanCotisationDto() {
    }

    public String getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(String idPlan) {
        this.idPlan = idPlan;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public BigDecimal getMontantAttendu() {
        return montantAttendu;
    }

    public void setMontantAttendu(BigDecimal montantAttendu) {
        this.montantAttendu = montantAttendu;
    }

    public FrequenceCotisation getFrequence() {
        return frequence;
    }

    public void setFrequence(FrequenceCotisation frequence) {
        this.frequence = frequence;
    }

    public Integer getDureeJours() {
        return dureeJours;
    }

    public void setDureeJours(Integer dureeJours) {
        this.dureeJours = dureeJours;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public BigDecimal getTauxPenaliteRetard() {
        return tauxPenaliteRetard;
    }

    public void setTauxPenaliteRetard(BigDecimal tauxPenaliteRetard) {
        this.tauxPenaliteRetard = tauxPenaliteRetard;
    }

    public StatutPlanCotisation getStatut() {
        return statut;
    }

    public void setStatut(StatutPlanCotisation statut) {
        this.statut = statut;
    }
}